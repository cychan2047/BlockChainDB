# README:

## Author
-Chuyang Chen NUID: 002756503
-Yixiao Chen NUID: 002672046 

## Overview

This Java application implements a file-based NoSQL database management system with B-tree as its indexing structure. It features a command-line interface (CLI) for database interactions, supporting basic operations such as open, put, get, remove, find, quit and so on.

## Known Bugs

- The underlying data value is truncated before stored into data blocks.
- When intput size is high, it could cause exceeding error for put and find.

## Limitations

- The system utilizes a basic CLI without graphical interface.
- No utilization of memory, compromising efficiency. 
- Single-user access and does not support concurrent operations.
- Single directory file storage, not accounting for distributed environments.
- Handles only text data. Binary data or complex data types are unsupported.

## Assumptions

- Block and file sizes are fixed and do not adapt to the dataset's size or requirements.
- Data file size are within 6digits. Otherwise not yet tested. 
- Users are expected to provide correct input with prompt for application to work.

## Future Enhancements

To improve this system for broader usage:

- Implement comprehensive error handling and logging mechanisms.
- Support various data types and complex data structures.
- Enable distributed file storage and management for scalability.

