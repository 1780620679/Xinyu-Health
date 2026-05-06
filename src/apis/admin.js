// mock 用户数据
const mockUsers = [
  {
    id: 1,
    username: 'admin',
    password: '123456',
    userType: 2,
    nickname: '管理员'
  },
  {
    id: 2,
    username: 'user',
    password: '123456',
    userType: 1,
    nickname: '普通用户'
  }
]

export const LoginAPI = (data) => {
  const user = mockUsers.find(u => u.username === data.username && u.password === data.password)
  
  if (user) {
    return Promise.resolve({
      token: `mock-token-${user.id}`,
      userInfo: {
        id: user.id,
        username: user.username,
        userType: user.userType,
        nickname: user.nickname
      }
    })
  } else {
    return Promise.reject({ msg: '用户名或密码错误' })
  }
}

export const LogoutAPI = () => {
  return Promise.resolve({ success: true })
}

export const RegisterAPI = (data) => {
  const exists = mockUsers.find(u => u.username === data.username)
  
  if (exists) {
    return Promise.reject({ msg: '用户名已存在' })
  } else {
    const newUser = {
      id: mockUsers.length + 1,
      username: data.username,
      password: data.password,
      userType: 1,
      nickname: data.nickname || data.username
    }
    mockUsers.push(newUser)
    return Promise.resolve({ success: true })
  }
}