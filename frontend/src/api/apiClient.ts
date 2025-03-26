import axios from 'axios';

const apiClient = axios.create({
  baseURL: (import.meta as any).env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export default apiClient;