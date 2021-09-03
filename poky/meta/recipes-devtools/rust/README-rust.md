## Introduction

This provides the Rust compiler, tools for building packages (cargo), and 
a few example projects.

## What works:

 - Building `rust-native` and `cargo-native`
 - Building Rust based projects with Cargo for the TARGET
   - e.g. `rustfmt` which is used by the CI system
 - `-buildsdk` and `-crosssdk` packages

## What doesn't:

 - Using anything but x86_64 or arm64 as the build environment
 - rust (built for target) [issue #81](https://github.com/meta-rust/meta-rust/issues/81)

## What's untested:

 - cargo (built for target)

## Building a rust package

When building a rust package in bitbake, it's usually easiest to build with
cargo using cargo.bbclass.  If the package already has a Cargo.toml file (most
rust packages do), then it's especially easy.  Otherwise you should probably
get the code building in cargo first. 

Once your package builds in cargo, you can use
[cargo-bitbake](https://github.com/cardoe/cargo-bitbake) to generate a bitbake
recipe for it.  This allows bitbake to fetch all the necessary dependent
crates, as well as a pegged version of the crates.io index, to ensure maximum
reproducibility. Once the Rust SDK support is added to oe-core, cargo-bitbake
may also be added to the SDK.

NOTE: You will have to edit the generated recipe based on the comments
contained within it

## TODO

## Pitfalls

 - TARGET_SYS _must_ be different from BUILD_SYS. This is due to the way Rust configuration options are tracked for different targets. This is the reason we use the Yocto triples instead of the native Rust triples. See rust-lang/cargo#3349.

## Dependencies

On the host:
 - Any `-sys` packages your project might need must have RDEPENDs for
 the native library.

On the target:
 - Any `-sys` packages your project might need must have RDEPENDs for
 the native library.

## Copyright

MIT OR Apache-2.0 - Same as rust

