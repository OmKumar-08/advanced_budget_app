import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import apiClient from '../../api/apiClient'
import { ReactNode } from 'react'

export interface Investment {
  return: number
  description: ReactNode
  id: number
  name: string
  amount: number
  type: 'STOCKS' | 'BONDS' | 'REAL_ESTATE' | 'CRYPTO' | 'OTHER'
  purchaseDate: string
  currentValue: number
  returnRate: number
  risk: 'LOW' | 'MEDIUM' | 'HIGH'
  notes?: string
  lastUpdated: string
}

interface InvestmentsState {
  items: Investment[]
  loading: boolean
  error: string | null
  totalValue: number
  totalReturn: number
}

const initialState: InvestmentsState = {
  items: [],
  loading: false,
  error: null,
  totalValue: 0,
  totalReturn: 0
}

export const fetchInvestments = createAsyncThunk(
  'investments/fetchInvestments',
  async () => {
    const response = await apiClient.get('/investments')
    return response.data
  }
)

export const createInvestment = createAsyncThunk(
  'investments/createInvestment',
  async (investment: Omit<Investment, 'id' | 'currentValue' | 'returnRate' | 'lastUpdated'>) => {
    const response = await apiClient.post('/investments', investment)
    return response.data
  }
)

export const updateInvestment = createAsyncThunk(
  'investments/updateInvestment',
  async ({ id, investment }: { id: number; investment: Partial<Investment> }) => {
    const response = await apiClient.put(`/investments/${id}`, investment)
    return response.data
  }
)

export const deleteInvestment = createAsyncThunk(
  'investments/deleteInvestment',
  async (id: number) => {
    await apiClient.delete(`/investments/${id}`)
    return id
  }
)

const calculateTotals = (investments: Investment[]) => {
  return investments.reduce(
    (acc, investment) => {
      acc.totalValue += investment.currentValue
      acc.totalReturn += (investment.currentValue - investment.amount)
      return acc
    },
    { totalValue: 0, totalReturn: 0 }
  )
}

const investmentsSlice = createSlice({
  name: 'investments',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchInvestments.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchInvestments.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
        const totals = calculateTotals(action.payload)
        state.totalValue = totals.totalValue
        state.totalReturn = totals.totalReturn
      })
      .addCase(fetchInvestments.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message || 'Failed to fetch investments'
      })
      .addCase(createInvestment.fulfilled, (state, action) => {
        state.items.push(action.payload)
        const totals = calculateTotals(state.items)
        state.totalValue = totals.totalValue
        state.totalReturn = totals.totalReturn
      })
      .addCase(updateInvestment.fulfilled, (state, action) => {
        const index = state.items.findIndex((item) => item.id === action.payload.id)
        if (index !== -1) {
          state.items[index] = action.payload
          const totals = calculateTotals(state.items)
          state.totalValue = totals.totalValue
          state.totalReturn = totals.totalReturn
        }
      })
      .addCase(deleteInvestment.fulfilled, (state, action) => {
        state.items = state.items.filter((item) => item.id !== action.payload)
        const totals = calculateTotals(state.items)
        state.totalValue = totals.totalValue
        state.totalReturn = totals.totalReturn
      })
  }
})

export default investmentsSlice.reducer