import React, { useEffect } from 'react'
import {
  Grid,
  Paper,
  Typography,
  Box,
  CircularProgress,
  Card,
  CardContent,
  Divider
} from '@mui/material'
import {
  AccountBalance as AccountBalanceIcon,
  TrendingUp as TrendingUpIcon,
  Payment as PaymentIcon,
  Group as GroupIcon
} from '@mui/icons-material'
import { Line } from 'react-chartjs-2'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import { useAppDispatch, useAppSelector } from '../hooks/redux'
import { fetchTransactions } from '../store/slices/transactionsSlice'
import { fetchInvestments } from '../store/slices/investmentsSlice'
import { fetchLoans } from '../store/slices/loansSlice'
import { RootState } from '@/store/store'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

const Dashboard: React.FC = () => {
  const dispatch = useAppDispatch()
  const transactions = useAppSelector((state: RootState) => state.transactions)
  const investments = useAppSelector((state) => state.investments)
  const loans = useAppSelector((state) => state.loans)

  useEffect(() => {
    dispatch(fetchTransactions())
    dispatch(fetchInvestments())
    dispatch(fetchLoans())
  }, [])

  const loading = (transactions as any ).loading || (investments as any).loading || (loans as any).loading

  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100%'
        }}
      >
        <CircularProgress />
      </Box>
    )
  }

  const totalIncome = (transactions as any ).items
    .filter((t: { type: string }) => t.type === 'INCOME')
    .reduce((sum: any, t: { amount: any }) => sum + t.amount, 0)

  const totalExpenses = (transactions as any ).items
    .filter((t: { type: string }) => t.type === 'EXPENSE')
    .reduce((sum: any, t: { amount: any }) => sum + t.amount, 0)

  const monthlyData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Income',
        data: [65, 59, 80, 81, 56, 55],
        fill: false,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1
      },
      {
        label: 'Expenses',
        data: [28, 48, 40, 19, 86, 27],
        fill: false,
        borderColor: 'rgb(255, 99, 132)',
        tension: 0.1
      }
    ]
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <Grid container spacing={3}>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  mb: 1
                }}
              >
                <AccountBalanceIcon
                  sx={{ color: 'primary.main', mr: 1 }}
                />
                <Typography variant="h6">Balance</Typography>
              </Box>
              <Typography variant="h4">
                ${(totalIncome - totalExpenses).toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  mb: 1
                }}
              >
                <TrendingUpIcon
                  sx={{ color: 'success.main', mr: 1 }}
                />
                <Typography variant="h6">Investments</Typography>
              </Box>
              <Typography variant="h4">
                ${(investments as any).totalValue?.toFixed(2) || '0.00'}
              </Typography>
              <Typography
                variant="body2"
                color={(investments as any).totalReturn >= 0 ? 'success.main' : 'error.main'}
              >
                Return: ${(investments as any).totalReturn.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  mb: 1
                }}
              >
                <PaymentIcon sx={{ color: 'error.main', mr: 1 }} />
                <Typography variant="h6">Loans</Typography>
              </Box>
              <Typography variant="h4">
                ${(loans as any).totalDebt.toFixed(2)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Monthly: ${(loans as any).totalMonthlyPayments.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  mb: 1
                }}
              >
                <GroupIcon sx={{ color: 'secondary.main', mr: 1 }} />
                <Typography variant="h6">Group Expenses</Typography>
              </Box>
              <Typography variant="h4">
                $
                {(transactions as any ).items
                  .filter((t: { groupId: any; isSettled: any }) => t.groupId && !t.isSettled)
                  .reduce((sum: any, t: { amount: any }) => sum + t.amount, 0)
                  .toFixed(2)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Pending Settlements
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Monthly Overview
            </Typography>
            <Divider sx={{ mb: 2 }} />
            <Box sx={{ height: 300 }}>
              <Line
                data={monthlyData}
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
          </Paper>
        </Grid>
      </Grid>
    </Box>
  )
}

export default Dashboard