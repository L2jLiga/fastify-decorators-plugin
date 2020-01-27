# Fastify decorators support plugin

This plugin provides better [`fastify-decorators`] package support by IntelliJ IDEA

## Things to be done

- Generic
   - [ ] Disable all inspection when [`fastify-decorators`] package not found

- Implicit usage providers
   - [ ] `@Controller` and `@Service` constructors used implicitly
   - [x] `@Controller` classes default export used implicitly
   - [ ] Usage providers covered by tests

- `@Controller` classes export inspections
   - [x] Raise error when class does not have default export
   - [x] Provide quick-fix for default export
   - [ ] Correctly handle case when class does not have export statement in attributes list
   - [ ] Inspection covered by tests

- Controller/Service constructor inspections
   - [x] Injectable services type should be class
   - [ ] Provide quick-fix when interface used
   - [ ] Injectable services should have `@Service` annotation
   - [ ] Provide quick-fix when class does not has `@Service` annotation
   - [ ] Inspection covered by tests

- Classes decorators inspections:
   - [ ] Class should have one of `@Service`, `@Controller`, `@GET`, `@POST` etc decorators
   - [ ] Provide quick-fix when multiple decorators used


[`fastify-decorators`]: https://npmjs.org/package/fastify-decorators