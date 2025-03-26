import React, { useEffect, useState } from 'react'
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  CircularProgress
} from '@mui/material'
import { Line } from 'react-chartjs-2'
import { useAppDispatch, useAppSelector } from '../hooks/redux'
import { fetchLoans } from '../store/slices/loansSlice'

interface LoanFormData {
  type: string
  amount: number
  interestRate: number
  term: number
  description: string
}

const loanTypes = [
  { value: 'PERSONAL', label: 'Personal Loan' },
  { value: 'MORTGAGE', label: 'Mortgage' },
  { value: 'AUTO', label: 'Auto Loan' },
  { value: 'STUDENT', label: 'Student Loan' },
  { value: 'BUSINESS', label: 'Business Loan' }
]

const Loans: React.FC = () => {
  const dispatch = useAppDispatch()
  const { items: loans, loading, totalDebt, totalMonthlyPayments } = useAppSelector((state) => state.loans as unknown as { items: Loan[], loading: boolean, totalDebt: number, totalMonthlyPayments: number })
  const [openDialog, setOpenDialog] = useState(false)
  const [formData, setFormData] = useState<LoanFormData>({
    type: 'PERSONAL',
    amount: 0,
    interestRate: 0,
    term: 12,
    description: ''
  })

  useEffect(() => {
    dispatch(fetchLoans())
  }, [])

  const handleOpenDialog = () => setOpenDialog(true)
  const handleCloseDialog = () => setOpenDialog(false)

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: ['amount', 'interestRate', 'term'].includes(name) ? Number(value) : value
    }))
  }

  const handleSubmit = () => {
    // TODO: Implement loan creation
    handleCloseDialog()
  }

  const calculateMonthlyPayment = (loan: Loan) => {
    const monthlyRate = loan.interestRate / 12 / 100
    const monthlyPayment = 
      (loan.amount * monthlyRate * Math.pow(1 + monthlyRate, loan.term)) /
      (Math.pow(1 + monthlyRate, loan.term) - 1)
    return monthlyPayment
  }

  const paymentScheduleData = {
    labels: loans.map((loan: Loan) => loan.description),
    datasets: [
      {
        label: 'Monthly Payments',
        data: loans.map((loan: Loan) => calculateMonthlyPayment(loan)),
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgb(75, 192, 192)',
        borderWidth: 1
      }
    ]
  }

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
        <CircularProgress />
      </Box>
    )
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Loan Management</Typography>
        <Button variant="contained" color="primary" onClick={handleOpenDialog}>
          Add Loan
        </Button>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6">Total Outstanding Debt</Typography>
              <Typography variant="h4">${totalDebt.toFixed(2)}</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6">Total Monthly Payments</Typography>
              <Typography variant="h4">${totalMonthlyPayments.toFixed(2)}</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Monthly Payment Schedule
              </Typography>
              <Box sx={{ height: 300 }}>
                <Line
                  data={paymentScheduleData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: {
                        position: 'top' as const
                      }
                    },
                    scales: {
                      y: {
                        beginAtZero: true,
                        title: {
                          display: true,
                          text: 'Monthly Payment ($)'
                        }
                      }
                    }
                  }}
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {loans.map((loan: Loan) => (
          <Grid item xs={12} md={6} key={loan.id}>
            <Card>
              <CardContent>
                <Typography variant="h6">{loan.description}</Typography>
                <Typography color="textSecondary">{loan.type}</Typography>
                <Box sx={{ mt: 2 }}>
                  <Typography>Principal: ${loan.amount.toFixed(2)}</Typography>
                  <Typography>Interest Rate: {loan.interestRate}%</Typography>
                  <Typography>Term: {loan.term} months</Typography>
                  <Typography>Monthly Payment: ${calculateMonthlyPayment(loan).toFixed(2)}</Typography>
                  <Typography>Remaining Balance: ${loan.remainingBalance.toFixed(2)}</Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Add New Loan</DialogTitle>
        <DialogContent>
          <TextField
            select
            fullWidth
            margin="normal"
            name="type"
            label="Loan Type"
            value={formData.type}
            onChange={handleInputChange}
          >
            {loanTypes.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            fullWidth
            margin="normal"
            name="amount"
            label="Loan Amount"
            type="number"
            value={formData.amount}
            onChange={handleInputChange}
          />
          <TextField
            fullWidth
            margin="normal"
            name="interestRate"
            label="Interest Rate (%)"
            type="number"
            value={formData.interestRate}
            onChange={handleInputChange}
          />
          <TextField
            fullWidth
            margin="normal"
            name="term"
            label="Term (months)"
            type="number"
            value={formData.term}
            onChange={handleInputChange}
          />
          <TextField
            fullWidth
            margin="normal"
            name="description"
            label="Description"
            value={formData.description}
            onChange={handleInputChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            Add Loan
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default Loans


interface Loan {
  id: string
  description: string
  type: string
  amount: number
  interestRate: number
  term: number
  remainingBalance: number
}