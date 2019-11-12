# Message Board
- A client can create a message in the service
- A client can modify their own messages
- A client can delete their own messages
- A client can view all messages in the service

# Resources
## Message

{
  "client" : <number>
  "text"   : <string>
  "id"     : <number>
  "links"  : [
    {
      "href": "/board/<client>/<id>",
      "rel": "Delete",
      "method": "DELETE"
    },
    {
      "href": "/board/<client>/<id>",
      "rel": "Modify",
      "method": "PUT"
    }    
  ]
}

# API
## Get to root
### Request
GET /
Accept: application/json

### Response
200 OK
Content-Type: application/json

{
  "client" : <number>
  "links"  : [
    {
      "href": "/board",
      "rel": "list",
      "method": "GET"
    },
    {
      "href": "/board/<client>",
      "rel": "create",
      "method": "POST"
    }    
  ]
}

## A client can create a message in the service
### Request
POST /board/<client>
Accept: application/json

{
  "client"  = <number>
  "text" =  <string>
}

### Response
201 Created
Content-Type: application/json

{
  "client" : <number>
  "text"   : <string>
  "ID"     : <number>
  "links"  : [
    {
      "href": "/board/<client>/<id>",
      "rel": "self",
      "method": "GET"
    }
]
}

401 Unauthorized

## A client can modify their own messages
### Request
PUT /board/<client>/<message>
Accept: application/json

{
  "client"  = <number>
  "text" =  <string>
}

### Response
200 OK
Content-Type: application/json

{
  "client" : <number>
  "previous_text"   : <string>
  "id"     : <number>
  "links"  : [
    {
      "href": "/board/<client>/<id>",
      "rel": "self",
      "method": "GET"
    }
]
}

401 Unauthorized
404 Not found

## A client can delete their own messages
### Request
DELETE /board/<client>/<message>
Accept: application/json

### Response
200 OK
Content-Type: application/json

{
  "client" : <number>
  "text"   : <string>
  "id"     : <number>
  "links"  : [
    {
      "href": "/board/<client>/<id>",
      "rel": "self",
      "method": "GET"
    }
]
}

401 Not authorized
404 Not found

## A client can view all messages in the service
### Request
GET /board
Accept: application/json

### Response
200 OK
Content-Type: application/json

{
  "messages" : [
    // An example of a message created by the client
    {
      "client" : <number>
      "text"   : <string>
      "id"     : <number>
      "links"  : [
        {
          "href": "/board/<client>/<id>",
          "rel": "delete",
          "method": "DELETE"
        },
        {
          "href": "/board/<client>/<id>",
          "rel": "edit",
          "method": "PUT"
        }    
      ]
    },
    // An example of a message created by another client
    {
      "client" : <number>
      "text"   : <string>
      "id"     : <number>
      "links"  : [
        {
          "href": "/board/<client>/<id>",
          "rel": "self",
          "method": "GET"
        }    
      ]
    }
  ]
}

# Get one message
## Request
GET /board/<client>/<id>
Accept: application/json

## Response

200 OK
Content-Type: application/json

    {
      "client" : <number>
      "text"   : <string>
      "id"     : <number>
      "links"  : [
        {
          "href": "/board/<client>/<id>",
          "rel": "delete",
          "method": "DELETE"
        },
        {
          "href": "/board/<client>/<id>",
          "rel": "edit",
          "method": "PUT"
        }    
      ]
    },



401 Unauthorized
404 Not found
