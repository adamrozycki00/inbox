# Inbox

This repository contains the backend implementation for an application that enables users to send messages to other users' inboxes.

Each inbox is associated with a specific topic, allowing users to reply to that topic. Only the inbox owner can view the replies. Users don't need to register or log in to send messages; they simply provide their credentials (username and secret). The credentials are securely hashed into a unique signature, which is used to identify the user.

An inbox can also be configured to allow anonymous messages, further simplifying the process for users who wish to reply.

The application is built in Java, following the Ports and Adapters architecture.
