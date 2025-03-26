import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { Box } from '@mui/material'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'

import Transactions from './pages/Transactions'
import Investments from './pages/Investments'
import Loans from './pages/Loans'
import Groups from './pages/Groups'
// TODO: Create and import Profile component once implemented
 import Profile from './pages/Profile'
import Login from './pages/Login'
import Register from './pages/Register'
import { useAppSelector } from './hooks/redux'

const App: React.FC = () => {
  const { isAuthenticated  } = useAppSelector((state) => state.user)

  return (
    <Box sx={{ height: '100vh', display: 'flex' }}>
      <Routes>
        <Route
          path="/"
          element={
            isAuthenticated ? (
              <Layout />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        >
          <Route index element={<Dashboard />} />
          <Route path="transactions" element={<Transactions />} />
          <Route path="investments" element={<Investments />} />
          <Route path="loans" element={<Loans />} />
          <Route path="groups" element={<Groups />} />
          <Route path="profile" element={<Profile />} />
        </Route>
        <Route
          path="/login"
          element={
            !isAuthenticated ? (
              <Login />
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/register"
          element={
            !isAuthenticated ? (
              <Register />
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
      </Routes>
    </Box>
  )
}

export default App