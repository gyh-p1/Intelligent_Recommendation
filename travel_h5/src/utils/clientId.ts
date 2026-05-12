const CLIENT_ID_KEY = 'travel_h5_client_id'

export function getClientId() {
  const cached = localStorage.getItem(CLIENT_ID_KEY)

  if (cached) {
    return cached
  }

  const clientId = `h5_${Date.now()}_${Math.random().toString(36).slice(2, 10)}`
  localStorage.setItem(CLIENT_ID_KEY, clientId)

  return clientId
}
