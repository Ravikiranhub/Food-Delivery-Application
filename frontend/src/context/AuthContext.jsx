import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import api from '../services/api.js';

const AuthContext = createContext(null);

function readStoredUser() {
  const stored = localStorage.getItem('foodhub_user');
  if (!stored) return null;
  try {
    return normalizeUser(JSON.parse(stored));
  } catch {
    localStorage.removeItem('foodhub_user');
    return null;
  }
}

function normalizeUser(rawUser) {
  if (!rawUser) return null;
  const roles = Array.isArray(rawUser.roles)
    ? rawUser.roles
    : rawUser.roles
      ? Object.values(rawUser.roles)
      : [];
  return { ...rawUser, roles: roles.map((role) => String(role).replace(/^ROLE_/, '')) };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    const clearSession = () => {
      localStorage.removeItem('foodhub_token');
      localStorage.removeItem('foodhub_user');
      if (active) {
        setUser(null);
        setLoading(false);
      }
    };

    window.addEventListener('foodhub-auth-expired', clearSession);

    const token = localStorage.getItem('foodhub_token');
    if (token) {
      api.get('/auth/me')
        .then(({ data }) => {
          if (!active) return;
          const normalized = normalizeUser(data);
          localStorage.setItem('foodhub_user', JSON.stringify(normalized));
          setUser(normalized);
        })
        .catch(clearSession)
        .finally(() => {
          if (active) setLoading(false);
        });
    } else {
      clearSession();
    }

    return () => {
      active = false;
      window.removeEventListener('foodhub-auth-expired', clearSession);
    };
  }, []);

  const login = async (payload) => {
    localStorage.removeItem('foodhub_token');
    localStorage.removeItem('foodhub_user');
    const { data } = await api.post('/auth/login', payload);
    const normalized = normalizeUser(data.user);
    localStorage.setItem('foodhub_token', data.accessToken);
    localStorage.setItem('foodhub_user', JSON.stringify(normalized));
    setUser(normalized);
    setLoading(false);
    return normalized;
  };

  const register = async (payload) => {
    localStorage.removeItem('foodhub_token');
    localStorage.removeItem('foodhub_user');
    const { data } = await api.post('/auth/register', payload);
    const normalized = normalizeUser(data.user);
    localStorage.setItem('foodhub_token', data.accessToken);
    localStorage.setItem('foodhub_user', JSON.stringify(normalized));
    setUser(normalized);
    setLoading(false);
    return normalized;
  };

  const refreshMe = async () => {
    const { data } = await api.get('/auth/me');
    const normalized = normalizeUser(data);
    localStorage.setItem('foodhub_user', JSON.stringify(normalized));
    setUser(normalized);
    return normalized;
  };

  const logout = () => {
    localStorage.removeItem('foodhub_token');
    localStorage.removeItem('foodhub_user');
    setUser(null);
    setLoading(false);
  };

  const hasRole = (role) => user?.roles?.includes(role);

  const value = useMemo(() => ({ user, loading, login, register, logout, refreshMe, hasRole }), [user, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
