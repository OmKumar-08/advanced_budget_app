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
  CircularProgress,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Chip
} from '@mui/material'
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Check as CheckIcon
} from '@mui/icons-material'
import { useAppDispatch, useAppSelector } from '../hooks/redux'
import { createGroupTransaction, fetchTransactions, markTransactionSettled, Transaction } from '../store/slices/transactionsSlice'

interface GroupExpenseFormData {
  description: string
  amount: number
  splitType: string
  participants: string[]
}

const splitTypes = [
  { value: 'EQUAL', label: 'Split Equally' },
  { value: 'PERCENTAGE', label: 'Split by Percentage' },
  { value: 'AMOUNT', label: 'Split by Amount' }
]

const Groups: React.FC = () => {
  const dispatch = useAppDispatch()
 const { items: transactions, loading } = useAppSelector((state) => state.transactions)
  const [openDialog, setOpenDialog] = useState(false)
  const [formData, setFormData] = useState<GroupExpenseFormData>({
    description: '',
    amount: 0,
    splitType: 'EQUAL',
    participants: []
  })

  useEffect(() => {
    dispatch(fetchTransactions())
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

  const handleSubmit = async () => {
    if (!formData.description || formData.amount <= 0) {
      return
    }

    try {
      await dispatch(createGroupTransaction(formData))
      handleCloseDialog()
      setFormData({
        description: '',
        amount: 0,
        splitType: 'EQUAL',
        participants: []
      })
    } catch (error) {
      console.error('Failed to create group transaction:', error)
    }
  }

  const handleSettleUp = async (transactionId: number) => {
    if (isNaN(transactionId)) {
      console.error('Invalid transaction ID type');
      return;
    }
    try {
      await dispatch(markTransactionSettled(transactionId))
    } catch (error) {
      console.error('Failed to settle transaction:', error)
    }
  }


const groupedTransactions = transactions.filter((t: Transaction): t is Transaction & { groupId: number } => t.groupId !== undefined)
  const totalUnsettled = groupedTransactions
    .filter((t: Transaction) => !t.isSettled)
    .reduce((sum: number, t: Transaction) => sum + t.amount, 0)

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
        <Typography variant="h4">Group Expenses</Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={handleOpenDialog}
        >
          Add Group Expense
        </Button>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6">Total Unsettled Amount</Typography>
              <Typography variant="h4">${totalUnsettled.toFixed(2)}</Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6">Active Groups</Typography>
              <Typography variant="h4">
                {new Set(groupedTransactions.map((t: Transaction & { groupId: number }) => t.groupId)).size}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Group Expenses
              </Typography>
              <List>
                {groupedTransactions.map((transaction: { id: React.Key | null | undefined; isSettled: any; description: string | number | boolean | React.ReactElement<any, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined; amount: number; date: string | number | Date }) => (
                  <ListItem
                    key={transaction.id}
                    divider
                    secondaryAction={
                      !transaction.isSettled && (
                        <IconButton
                          edge="end"
                          aria-label="settle"
                          onClick={() => handleSettleUp(Number(transaction.id))}
                        >
                          <CheckIcon />
                        </IconButton>
                      )
                    }
                  >
                    <ListItemText
                      primary={transaction.description}
                      secondary={
                        <>
                          <Typography
                            component="span"
                            variant="body2"
                            color="text.primary"
                          >
                            ${transaction.amount.toFixed(2)}
                          </Typography>
                          {' — '}
                          {new Date(transaction.date).toLocaleDateString()}
                        </>
                      }
                    />
                    <Box sx={{ mr: 2 }}>
                      <Chip
                        label={transaction.isSettled ? 'Settled' : 'Pending'}
                        color={transaction.isSettled ? 'success' : 'warning'}
                        size="small"
                      />
                    </Box>
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Add Group Expense</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            margin="normal"
            name="description"
            label="Description"
            value={formData.description}
            onChange={handleInputChange}
          />
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
            select
            fullWidth
            margin="normal"
            name="splitType"
            label="Split Type"
            value={formData.splitType}
            onChange={handleInputChange}
          >
            {splitTypes.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
          {/* TODO: Add participant selection */}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            Add Expense
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default Groups
