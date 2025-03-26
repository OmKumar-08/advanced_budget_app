import { AuthState, NotificationsState } from './slices/userSlice';
import { LoansState } from './slices/loansSlice';

interface TransactionsState {
  items: any[];
  loading: boolean;
}

interface InvestmentsState {
  items: any[];
  loading: boolean;
  totalValue: number;
  totalReturn: number;
}

interface RootState {
  transactions: TransactionsState;
  investments: InvestmentsState;
  loans: LoansState;
  user: AuthState;
  notifications: NotificationsState;
}