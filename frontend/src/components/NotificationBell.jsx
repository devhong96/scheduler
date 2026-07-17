import { useEffect, useState } from 'react'
import {
  fetchNotifications,
  readNotification,
  readAllNotifications,
} from '../api/notification'

// 종 아이콘 + 안 읽은 개수 배지, 클릭 시 알림 목록 드롭다운
export default function NotificationBell() {
  const [items, setItems] = useState([])
  const [open, setOpen] = useState(false)

  const load = () => fetchNotifications().then(setItems).catch(() => {})

  useEffect(() => {
    load()
  }, [])

  const unread = items.filter((n) => !n.checked).length

  const onRead = async (id) => {
    await readNotification(id)
    load()
  }
  const onReadAll = async () => {
    await readAllNotifications()
    load()
  }

  return (
    <div style={styles.wrap}>
      <button style={styles.bell} onClick={() => setOpen((o) => !o)} title="알림">
        🔔
        {unread > 0 && <span style={styles.badge}>{unread}</span>}
      </button>

      {open && (
        <div style={styles.panel}>
          <div style={styles.head}>
            <b>알림</b>
            {unread > 0 && (
              <button style={styles.readAll} onClick={onReadAll}>
                모두 읽음
              </button>
            )}
          </div>

          {items.length === 0 ? (
            <p style={styles.empty}>알림이 없습니다.</p>
          ) : (
            <ul style={styles.list}>
              {items.map((n) => (
                <li key={n.id} style={{ ...styles.item, ...(n.checked ? styles.readItem : {}) }}>
                  <div style={styles.msg}>{n.message}</div>
                  <div style={styles.metaRow}>
                    <span style={styles.time}>
                      {n.createdDate?.slice(0, 16).replace('T', ' ')}
                    </span>
                    {!n.checked && (
                      <button style={styles.readBtn} onClick={() => onRead(n.id)}>
                        읽음
                      </button>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  )
}

const styles = {
  wrap: { position: 'relative' },
  bell: { position: 'relative', border: 'none', background: 'transparent', fontSize: 20, cursor: 'pointer', lineHeight: 1 },
  badge: { position: 'absolute', top: -6, right: -8, background: '#dc2626', color: '#fff', borderRadius: 10, fontSize: 11, minWidth: 16, height: 16, display: 'inline-flex', alignItems: 'center', justifyContent: 'center', padding: '0 4px' },
  panel: { position: 'absolute', right: 0, top: 32, width: 320, maxHeight: 400, overflowY: 'auto', background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, boxShadow: '0 6px 20px rgba(0,0,0,0.12)', zIndex: 20 },
  head: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 14px', borderBottom: '1px solid #eee' },
  readAll: { border: 'none', background: 'transparent', color: '#2563eb', cursor: 'pointer', fontSize: 13 },
  empty: { padding: 20, textAlign: 'center', color: '#9ca3af', fontSize: 14 },
  list: { listStyle: 'none', margin: 0, padding: 0 },
  item: { padding: '10px 14px', borderBottom: '1px solid #f3f4f6', fontSize: 14 },
  readItem: { opacity: 0.5 },
  msg: { marginBottom: 4 },
  metaRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  time: { color: '#9ca3af', fontSize: 12 },
  readBtn: { border: '1px solid #ddd', background: '#fff', borderRadius: 6, fontSize: 12, padding: '2px 8px', cursor: 'pointer' },
}
