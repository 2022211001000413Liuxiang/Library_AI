// 系统设置相关API

// 获取系统设置
export function getSettings() {
  return new Promise((resolve) => {
    const settings = localStorage.getItem('systemSettings')
    if (settings) {
      resolve({ data: JSON.parse(settings) })
    } else {
      resolve({
        data: {
          systemName: '图书馆管理系统',
          systemDesc: '一款功能完善的图书馆管理系统',
          pageSize: 10,
          refreshInterval: 0,
          theme: 'light',
          primaryColor: '#409EFF',
          sidebarCollapsed: false,
          showBreadcrumb: true,
          tableStripe: true,
          notifyAnnouncement: true,
          notifyDueDate: true,
          notifyOverdue: true,
          notifyNewBook: false,
          remindDays: 3,
          verifyCode: false,
          lockOnFail: true,
          lockThreshold: 5,
          lockDuration: 30,
          sessionTimeout: 60
        }
      })
    }
  })
}

// 保存系统设置
export function saveSettings(data) {
  return new Promise((resolve) => {
    localStorage.setItem('systemSettings', JSON.stringify(data))
    resolve({ success: true, message: '设置保存成功' })
  })
}
