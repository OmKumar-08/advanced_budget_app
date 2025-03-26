import {  AnyAction, configureStore } from '@reduxjs/toolkit'
import userReducer from './slices/userSlice'
import notificationsReducer from './slices/notificationsSlice'
import transactionsReducer from './slices/transactionsSlice'
import investmentsReducer from './slices/investmentsSlice'
import loansReducer from './slices/loansSlice'

const store = configureStore({
  reducer: {
    user: userReducer,
    notifications: notificationsReducer,
    transactions: transactionsReducer,
    investments: investmentsReducer,
    loans: loansReducer
  }
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

export default store

