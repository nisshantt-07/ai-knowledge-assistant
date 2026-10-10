import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

export const loginUser = async (email, password) => {
  const response = await axios.post(`${API_BASE_URL}/users/login`, {
    email,
    password
  })
  return response.data
}

export const signupUser = async (name, email, password) => {
  const response = await axios.post(`${API_BASE_URL}/users/signup`, {
    name,
    email,
    password,
    role: 'EMPLOYEE'
  })
  return response.data
}
