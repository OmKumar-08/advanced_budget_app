import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import apiClient from '../../api/apiClient'

export interface Notification {
  id: number
  type: 'INFO' | 'SUCCESS' | 'WARNING' | 'ERROR'
  title: string
  message: string
  isRead: boolean
  createdAt: string
  userId: number
  relatedEntityType?: 'TRANSACTION' | 'INVESTMENT' | 'LOAN' | 'BILL'
  relatedEntityId?: number
}

interface NotificationsState {
  items: Notification[]
  loading: boolean
  error: string | null
  unreadCount: number
}

const initialState: NotificationsState = {
  items: [],
  loading: false,
  error: null,
  unreadCount: 0
}

export const fetchNotifications = createAsyncThunk(
  'notifications/fetchNotifications',
  async () => {
    const response = await apiClient.get('/notifications')
    return response.data
  }
)

export const markNotificationAsRead = createAsyncThunk(
  'notifications/markAsRead',
  async (id: number) => {
    await apiClient.put(`/notifications/${id}/mark-read`)
    return id
  }
)

export const markAllNotificationsAsRead = createAsyncThunk(
  'notifications/markAllAsRead',
  async () => {
    await apiClient.put('/notifications/mark-all-read')
    return true
  }
)

export const deleteNotification = createAsyncThunk(
  'notifications/delete',
  async (id: number) => {
    await apiClient.delete(`/notifications/${id}`)
    return id
  }
)

const notificationsSlice = createSlice({
  name: 'notifications',
  initialState,
  reducers: {
    addNotification: (state, action) => {
      state.items.unshift(action.payload)
      if (!action.payload.isRead) {
        state.unreadCount += 1
      }
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchNotifications.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchNotifications.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
        state.unreadCount = action.payload.filter(
          (notification: Notification) => !notification.isRead
        ).length
      })
      .addCase(fetchNotifications.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message || 'Failed to fetch notifications'
      })
      .addCase(markNotificationAsRead.fulfilled, (state, action) => {
        const notification = state.items.find((item) => item.id === action.payload)
        if (notification && !notification.isRead) {
          notification.isRead = true
          state.unreadCount -= 1
        }
      })
      .addCase(markAllNotificationsAsRead.fulfilled, (state) => {
        state.items.forEach((notification) => {
          notification.isRead = true
        })
        state.unreadCount = 0
      })
      .addCase(deleteNotification.fulfilled, (state, action) => {
        const notification = state.items.find((item) => item.id === action.payload)
        state.items = state.items.filter((item) => item.id !== action.payload)
        if (notification && !notification.isRead) {
          state.unreadCount -= 1
        }
      })
  }
})

export const { addNotification } = notificationsSlice.actions
export default notificationsSlice.reducer