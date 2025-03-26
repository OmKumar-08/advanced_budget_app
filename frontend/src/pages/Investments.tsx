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
import { fetchInvestments, Investment } from '../store/slices/investmentsSlice'
import { RootState } from '@/store/store'

interface InvestmentFormData {
  type: string
  amount: number
  description: string
}

const investmentTypes = [
  { value: 'STOCKS', label: 'Stocks' },
  { value: 'BONDS', label: 'Bonds' },
  { value: 'CRYPTO', label: 'Cryptocurrency' },
  { value: 'REAL_ESTATE', label: 'Real Estate' }
]

const Investments: React.FC = () => {
  const dispatch = useAppDispatch()
  const { items : investments, loading, totalValue, totalReturn } = useAppSelector((state: RootState) => state.investments)
  const [openDialog, setOpenDialog] = useState(false)
  const [formData, setFormData] = useState<InvestmentFormData>({
    type: 'STOCKS',
    amount: 0,
    description: ''
  })

  useEffect(() => {
    dispatch(fetchInvestments())
  }, [])

  const handleOpenDialog = () => setOpenDialog(true)
  const handleCloseDialog = () => setOpenDialog(false)

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'amount' ? Number(value) : value
    }))
  }

  const handleSubmit = () => {
    // TODO: Implement investment creation
    handleCloseDialog()
  }

  const performanceData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Portfolio Value',
        data: investments.map((inv: { currentValue: any }) => inv.currentValue),
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1
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
        <Typography variant="h4">Investment Portfolio</Typography>
        <Button variant="contained" color="primary" onClick={handleOpenDialog}>
          Add Investment
        </Button>
      </Box>

      <Grid container spacing={3}>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">Total Portfolio Value</Typography>
              <Typography variant="h4">${totalValue.toFixed(2)}</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">Total Return</Typography>
              <Typography
                variant="h4"
                color={totalReturn >= 0 ? 'success.main' : 'error.main'}
              >
                ${totalReturn.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">Number of Investments</Typography>
              <Typography variant="h4">{investments.length}</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Portfolio Performance
              </Typography>
              <Box sx={{ height: 300 }}>
                <Line
                  data={performanceData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: {
                        position: 'top' as const
                      }
                    }
                  }}
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

         {investments.map((investment: Investment) => (
          <Grid item xs={12} md={6} key={investment.id}>
            <Card>
              <CardContent>
                <Typography variant="h6">{investment.description}</Typography>
                <Typography color="textSecondary">{investment.type}</Typography>
                <Box sx={{ mt: 2 }}>
                  <Typography>Initial Investment: ${investment.amount.toFixed(2)}</Typography>
                  <Typography>Current Value: ${investment.currentValue.toFixed(2)}</Typography>
                  <Typography
                    color={investment.return >= 0 ? 'success.main' : 'error.main'}
                  >
                    Return: ${investment.return.toFixed(2)}
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid> 
      

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Add New Investment</DialogTitle>
        <DialogContent>
          <TextField
            select
            fullWidth
            margin="normal"
            name="type"
            label="Investment Type"
            value={formData.type}
            onChange={handleInputChange}
          >
            {investmentTypes.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            fullWidth
            margin="normal"
            name="amount"
            label="Amount"
            type="number"
            value={formData.amount}
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
            Add Investment
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default Investments