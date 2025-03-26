import apiClient from '@/api/apiClient'
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import axios from 'axios'

export interface Transaction {
  id: number
  amount: number
  description: string
  type: 'INCOME' | 'EXPENSE' | 'TRANSFER'
  category: string
  date: string
  isRecurring: boolean
  recurringInterval?: string
  isSettled: boolean
  groupId?: number
  participants?: string[]
}

interface TransactionsState {
  items: Transaction[]
  loading: boolean
  error: string | null
}

const initialState: TransactionsState = {
  items: [],
  loading: false,
  error: null
}

export const fetchTransactions = createAsyncThunk(
  'transactions/fetchTransactions',
  async () => {
    const response = await apiClient.get('/transactions')
    return response.data
  }
)

export const createTransaction = createAsyncThunk(
  'transactions/createTransaction',
  async (transaction: Omit<Transaction, 'id'>) => {
    const response = await apiClient.post('/transactions', transaction)
    return response.data
  }
)

export const updateTransaction = createAsyncThunk(
  'transactions/updateTransaction',
  async ({ id, transaction }: { id: number; transaction: Partial<Transaction> }) => {
    const response = await apiClient.put(`/transactions/${id}`, transaction)
    return response.data
  }
)

export const createGroupTransaction = createAsyncThunk(
  'transactions/createGroupTransaction',
  async (data: { description: string; amount: number; splitType: string; participants: string[] }) => {
    const response = await axios.post('/api/transactions/group', {
      ...data,
      type: 'EXPENSE',
      isSettled: false,
      date: new Date().toISOString()
    })
    return response.data
  }
)

export const markTransactionSettled = createAsyncThunk(
  'transactions/markSettled',
  async (id: number) => {
    if (typeof id !== 'number') {
      console.error('Invalid transaction ID');
      return;
    }
    await apiClient.post(`/transactions/${id}/mark-settled`)
    return id
  }
)

const transactionsSlice = createSlice({
  name: 'transactions',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchTransactions.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchTransactions.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
      })
      .addCase(fetchTransactions.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message || 'Failed to fetch transactions'
      })
      .addCase(createTransaction.fulfilled, (state, action) => {
        state.items.push(action.payload)
      })
      .addCase(updateTransaction.fulfilled, (state, action) => {
        const index = state.items.findIndex((item) => item.id === action.payload.id)
        if (index !== -1) {
          state.items[index] = action.payload
        }
      })
      .addCase(markTransactionSettled.fulfilled, (state, action) => {
        const transaction = state.items.find((item) => item.id === action.payload)
        if (transaction) {
          transaction.isSettled = true
        }
      })
      .addCase(createGroupTransaction.fulfilled, (state, action) => {
        state.items.push(action.payload)
      })
  }
})

export default transactionsSlice.reducer