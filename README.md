# VirtualThreadsTest
This project is a graphical Java application that visualizes and demonstrates multi-threading concepts, including the use of **virtual threads** (introduced in modern Java versions) and **platform threads**. The application simulates different threading behaviors by creating and animating circles on a panel, with options for configuring thread execution modes, thread counts, and their work profiles (such as inducing high CPU usage, memory usage, or performing blocking tasks).
### Key Features:
1. **Thread Visualization**:
    - Threads control the movement or behavior of circles, visually represented on the screen.
    - A side panel displays the currently running threads and their states.

2. **Thread Execution Modes**:
    - **Performance Mode**: Emphasizes the creation of many threads and quick execution.
    - **Scheduling Mode**: Focuses on animating circles across the screen.

3. **Thread Functionality**:
    - Users can configure what the threads do, such as simulating:
        - Blocking tasks with sleep.
        - High CPU-intensive operations.
        - High memory consumption tasks.

4. **Support for Virtual Threads**:
    - Demonstration and comparison of virtual threads to traditional platform threads.
    - Uses modern Java features to run lightweight virtual threads for scalable applications.

5. **Dynamic Configuration**:
    - Users can set the number of threads, toggle between virtual and platform threads, and choose operational modes via an interactive UI.

6. **Real-Time Updates**:
    - Threads and their states are dynamically displayed in a list.
    - The graphical layout automatically updates to reflect the ongoing thread activity.

The application is designed to serve as a teaching tool or demonstration project to showcase the differences in threading models and the performance impact of various threading tasks in real-time. It uses Swing for the graphical interface and modern Java concurrency features for thread management.
