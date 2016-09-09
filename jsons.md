# JSON Actions

Small JSON objects containing action ID and action parameters. Used to deliver events,
commands and/or other messages to other party.

## Common shortcuts

These are the common shortcuts used in API to minimize the size of 

### uid - User ID

Database ID of user. Integer.

### pid - Post ID

Database ID of post. Integer.

### cid - Comment ID

Database ID of comment. Integer.

### fid - Feed ID

Unique key of feed. One of following values:
- new
- featured

### msg - Message

Text with optional HTML. String.

### dat - Data / Payload

JSON Object with more properties.

### act - Action ID

One of the valid actions. String.

## Actions

### aus - Authentication Succeeded

Sent by: 
- server

Examples:
```json
{
  "act": "aus" // Action: Authentication Succeeded
}
```

### auf - Authentication Failed

Sent by:

Examples:
```json
{
  "act": "auf" // Action: Authentication Failed
}
```

### cdr - Client Details Report

Sent by:
- client

Examples:
```json
{
  "act": "cdr", // Action: Client Details Report
  "dat": {
    "brw": "Chrome", // Browser Application: Chrome
    "ver": "52",     // Browser Application Version: 52
    "os": "Android", // OS: Android
    "plf": "mobile"  // Platform: Mobile
  }
}
```

### cma - Comment Agree

Sent by:
- *client (planned)*
- server

Examples:
```json
{
  "act": "cma", // Action: Comment Agree
  "cid": 35896  // Comment ID: 35896
}
```

### cmd - Comment Disagree

Sent by:
- *client (planned)*
- server

Examples:
```json
{
  "act": "cmd", // Action: Comment Disagree
  "cid": 35896  // Comment ID: 35896
}
```

### gms - Global Message

Sent by:

Examples:
```json
{
  "act": "gms", // Action: Global Message
  "msg": "" // Message Content: ""
}
```

### msg - Message

Sent by:
- client
- server

Examples:
```json
 {
   "act": "msg", // Action: Message Send / Received
   "frm": 6869,  // From User Id (optional if client is sending): 6869
   "to": 3690,   // To User Id (optional if sever is sending): 3690
   "msg": "Message content here." // Content of message: "Message content here."
 }
```

### ntf - Notification

Sent by:
- server

Examples:
```json
{
  "act": "ntf", // Action: Notification
  "dat": {      // Notification Data / Payload (for more examples look at notifications protocol)
    "typ": "pcl", // Notification Type: Post Clap
    "pid": 56704, // Post ID: 56704
    "uid": 3690 // User ID (clapper): 3690 
  }
}
```

### pcl - Post Clap

Sent by:
- *client (planned)*
- server

Examples: 
```json
{
  "act": "pcl", // Action: Post Clap
  "pid": 56704 // Post ID: 56704
}
```

### pco - Post Comment

Sent by:
- *client (planned)*
- server

Examples: 
```json
{
  "act": "pco", // Action: Post Comment
  "pid": 56704, // Post ID: 56704
  "cid": 39645, // Comment ID (optional): 39645
  "uid": 3869,  // User (author) ID: 3869
  "msg": "Comment content here." // Comment Content: "Comment content here."
}
```

### sbr - Subscribe

Sent by:
- client

Examples:
```json
{
  "act": "sbr", // Action: Subscribe Request
  "pid": 56704 // Room: Post 56704
}
```

```json
{
  "act": "sbr", // Action: Subscribe Request
  "fid": "new" // Feed ID: "new"
}
```