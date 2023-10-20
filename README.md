# Basic IoC Container Implementation

**Note: This IoC container implementation is for educational purposes and not designed for practical usage in production applications. It does not handle circular dependencies.**

This is a basic implementation of an IoC (Inversion of Control) container in Java. The primary purpose of this project is to help you understand the fundamental concepts of IoC containers, dependency injection, and how they work in Java.

## Features

1. **Component Scanning**: The container can scan and discover classes annotated with `@Component` in your project.

2. **Dependency Injection**: It supports constructor-based dependency injection for the discovered components.

3. **Annotations**: You can use `@Autowired` for field injection and `@Qualifier` to specify which implementation to use when multiple implementations are available.

4. **Singleton Scope**: Components are created in the singleton scope by default. This means that the container maintains a single instance of each component.

5. **Default Implementation**: You can use the `@Default` annotation to specify the default implementation for an interface if there are multiple implementations. To specify the interfaces that a class should be the default implementation for, use the `@Default` annotation with an array of interface classes.

6. **Prototype Scope**: You can mark components with the `@Prototype` annotation to create a new instance each time it is requested.

## Usage


1. Create an instance of the `Container` class and call the `getService` method to retrieve your components.

2. The container handles dependency resolution and creates instances of your components as needed.

## Limitations

- This is a basic and minimalistic IoC container and is not intended for production use. It lacks many features and optimizations found in more mature IoC containers like Spring.

- Error handling and advanced features like aspect-oriented programming, custom scopes, and circular dependency handling are not implemented.
  ## Feel Free to Experiment

 i encourage you to experiment and learn with this IoC container. Explore the source code, make modifications, and leave a star if u can.




