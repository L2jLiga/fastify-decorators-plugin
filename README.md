# Fastify decorators support plugin

This plugin provides better [`fastify-decorators`] package support by IntelliJ IDEA

## Things to be done

- Generic
   - [x] Disable all inspection when [`fastify-decorators`] package not found
   - [ ] `@Service` decorator requires `reflect-metadata` included into project
   - [ ] `@Service` decorator requires `emitDecoratorsMetadata` ts option enabled
   - [ ] `@Controller` with constructor requires `emitDecoratorsMetadata` ts option enabled

- Implicit usage providers
   - [x] `@Controller` and `@Service` constructors used implicitly
   - [x] `@Controller` classes default export used implicitly
   - [ ] Usage providers covered by tests

- `@Controller` classes export inspections
   - [x] Raise error when class does not have default export
   - [x] Provide quick-fix for default export
   - [x] Correctly handle case when class does not have export statement in attributes list
   - [ ] Inspection covered by tests

- Controller/Service constructor inspections
   - [x] Injectable services type should be class
   - [ ] Provide quick-fix "Replace $interface with $implementation"
   - [x] Injectable services should have `@Service` annotation
   - [x] Provide quick-fix when class does not has `@Service` annotation
   - [ ] Inspection covered by tests

- Classes decorators inspections:
   - [ ] Class should have one of `@Service`, `@Controller`, `@GET`, `@POST` etc decorators
   - [ ] Provide quick-fix when multiple decorators used
   - [x] Only `@Controller` classes may have method/hook decorators
   - [ ] Provide quick-fix when class uses method/hook decorators
   - [ ] Only `@Controller` classes with `SINGLETON` type may have hook decorators
   - [ ] Provide quick-fix when class uses hook decorators and have type different to `SINGLETON`

## Contributing

We love contributors! This project is still WIP so any help would be amazing.

## License

This library is licensed under the Apache 2.0 License.

[`fastify-decorators`]: https://npmjs.org/package/fastify-decorators
