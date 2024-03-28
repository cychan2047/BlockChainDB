# README

## Author
- Chuyang Chen 
- Yixiao Chen 

## Overview

This Java application implements a file-based NoSQL database management system with B-tree as its indexing structure. It features a command-line interface (CLI) for database interactions, supporting basic operations such as open, put, get, remove, find, quit and so on.

## Known Bugs

- The underlying data value is truncated before stored into data blocks.
- No utilization of memory, compromising efficiency.
- Exception handling is minimal, leading to possible exits on I/O errors.

## Limitations

- The system utilizes a basic CLI without graphical interface.
- Single-user access and does not support concurrent operations.
- Single directory file storage, not accounting for distributed environments.
- Handles only text data; binary data or complex data types are unsupported.

## Assumptions

- Block and file sizes are fixed and do not adapt to the dataset's size or requirements.
- The system expects correct user input and lacks robust input validation.

## Future Enhancements

To improve this system for broader usage:

- Implement comprehensive error handling and logging mechanisms.
- Support various data types and complex data structures.
- Enable distributed file storage and management for scalability.

