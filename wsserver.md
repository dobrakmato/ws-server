
### Client

Entity representing one unique person, currently using the page.

Authentication State:
- authenticated (logged in)
- not authenticated (anonymous)

Automatic Properties:
- channel id (internal unique)

Properties:
- database id (internal database id, applicable only for authenticated users)
- analytics id (eaid / etuid - "unique" id stored in browser cookies, used to detect different browsers)
- client details:
  - browser
  - browser version
  - isMobile
  - platform
  - date of join
- nickname (string)

To get UID and Nickname, we have to do at least one mysql request or
 introduce some profile caching in redis (unpreffered, don't want to store
 passwords in cache server).
 
We should be able to retrieve all Rooms the Client is subscribed to.

### Room

Represents subscription point for receiving messages / events.

Properties:
- unique name / uid

We should be able to retrieve all Clients subscribing specific Room. 

### Subscription Point

Represents filter of events that have something similar. For example:
- events about the same post (similar post id)
- global events
- notifications (similar user id)

Subscription point can be: 
- **public** (global events, post events)
- protected (only for logged in users)
- **private** (user notifications, messages)

### Message

Object that consists of specific fields carrying information over websocket. They
 are usually represented as JSON strings.

Keys are often abbreviations or some characters to save bytes, when transferring
 messages over network.

#### Notification Data

### WebSocket Lifecycle

The following diagram represents WebSocket lifecycle.

1. Client -> Server: HTTP Upgrade Request (contains Cookies used to authenticate user).
2. Server -> Client: Complete HTTP Upgrade and do Handshake.
3. Client -> Server: Send User Agent details (browser, platform, os).
4. Server: Automatically subscribe to public Rooms.
   - global broadcast (reserved)
   - activity list (if required)
5. Server (optional): In the background, do authentication against database.
6. Server (optional): Automatically subscribe to personal Rooms.
   - notifications
   - messages (shouldn't be merged with notifications?)
7. Server -> Client (optional): Report authentication success.
8. Client -> Server: Request subscription to specific public Rooms.
   - post claps & comments (in post detail)
9. Server -> Client: Deliver messages / events from subscribed Rooms.
10. Client -> Server: Send messages to server.

Security Notes:

- We should also disconnect sockets on devices that haven't recently done anything to 
save out processing power and also their battery and data plan.
- We should also keep track of count of user subscriptions and **prevent** subscribing 
 too many rooms.
- We should also keep track of websockets from the **same IP address** and prevent creating
 too many connections from one IP.
 
In future:

- Deliver even more data using websockets where possible:
  - all comments
  - rendered pages
- Provide as alternative to REST API.
 
### Server API

We also need a way to publish events from our PHP application. 
*Needs research*.

```json
{
  "statistics": {
    "requests": {
      "bad": 56,
      "total": 31482
    },
    "connections": 31808,
    "maxClients": 560
  },
  "status": {
    "clients": {
      "count": 159,
      "browser": {
        "chrome": 56,
        "firefox": 15,
        "opera": 8,
        "edge": 1
      },
      "platform": {
        "desktop": 86,
        "mobile": 36
      },
      "os": {
        "windows": 45,
        "linux": 8,
        "osx": 24,
        "android": 6
      },
      "sessionDuration": 485.65
    },
    "threads": {
      "running": 4,
      "max": 8,
      "usage": 0.85
    }
  }
}
```