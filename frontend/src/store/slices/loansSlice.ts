import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import axios from 'axios'

export interface Loan {
  id: number
  name: string
  amount: number
  type: 'PERSONAL' | 'MORTGAGE' | 'AUTO' | 'STUDENT' | 'OTHER'
  startDate: string
  endDate: string
  interestRate: number
  remainingAmount: number
  monthlyPayment: number
  status: 'ACTIVE' | 'PAID' | 'DEFAULTED'
  lender: string
  notes?: string
}

export interface LoansState {
  items: Loan[]
  loading: boolean
  error: string | null
  totalDebt: number
  totalMonthlyPayments: number
}

const initialState: LoansState = {
  items: [],
  loading: false,
  error: null,
  totalDebt: 0,
  totalMonthlyPayments: 0
}

export const fetchLoans = createAsyncThunk(
  'loans/fetchLoans',
  async () => {
    const response = await axios.get('/api/loans')
    return response.data
  }
)

export const createLoan = createAsyncThunk(
  'loans/createLoan',
  async (loan: Omit<Loan, 'id'>) => {
    const response = await axios.post('/api/loans', loan)
    return response.data
  }
)

export const updateLoan = createAsyncThunk(
  'loans/updateLoan',
  async ({ id, loan }: { id: number; loan: Partial<Loan> }) => {
    const response = await axios.put(`/api/loans/${id}`, loan)
    return response.data
  }
)

export const deleteLoan = createAsyncThunk(
  'loans/deleteLoan',
  async (id: number) => {
    await axios.delete(`/api/loans/${id}`)
    return id
  }
)

const calculateTotals = (loans: Loan[]) => {
  return loans.reduce(
    (acc, loan) => {
      if (loan.status === 'ACTIVE') {
        acc.totalDebt += loan.remainingAmount
        acc.totalMonthlyPayments += loan.monthlyPayment
      }
      return acc
    },
    { totalDebt: 0, totalMonthlyPayments: 0 }
  )
}

const loansSlice = createSlice({
  name: 'loans',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchLoans.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchLoans.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
        const totals = calculateTotals(action.payload)
        state.totalDebt = totals.totalDebt
        state.totalMonthlyPayments = totals.totalMonthlyPayments
      })
      .addCase(fetchLoans.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message || 'Failed to fetch loans'
      })
      .addCase(createLoan.fulfilled, (state, action) => {
        state.items.push(action.payload)
        const totals = calculateTotals(state.items)
        state.totalDebt = totals.totalDebt
        state.totalMonthlyPayments = totals.totalMonthlyPayments
      })
      .addCase(updateLoan.fulfilled, (state, action) => {
        const index = state.items.findIndex((item) => item.id === action.payload.id)
        if (index !== -1) {
          state.items[index] = action.payload
          const totals = calculateTotals(state.items)
          state.totalDebt = totals.totalDebt
          state.totalMonthlyPayments = totals.totalMonthlyPayments
        }
      })
      .addCase(deleteLoan.fulfilled, (state, action) => {
        state.items = state.items.filter((item) => item.id !== action.payload)
        const totals = calculateTotals(state.items)
        state.totalDebt = totals.totalDebt
        state.totalMonthlyPayments = totals.totalMonthlyPayments
      })
  }
})

export default loansSlice.reducer