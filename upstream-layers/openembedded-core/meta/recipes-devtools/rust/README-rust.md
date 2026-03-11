# Introduction

This provides the Rust compiler, tools for building packages (cargo), and
a few example projects.

## Building a rust package

When building a rust package in bitbake, it's usually easiest to build with
cargo using cargo.bbclass.  If the package already has a Cargo.toml file (most
rust packages do), then it's especially easy. Otherwise you should probably
get the code building in cargo first.

Once your package builds in cargo, you can use
[cargo-bitbake](https://github.com/cardoe/cargo-bitbake) to generate a bitbake
recipe for it.  This allows bitbake to fetch all the necessary dependent
crates, as well as a pegged version of the crates.io index, to ensure maximum
reproducibility. Once the Rust SDK support is added to oe-core, cargo-bitbake
may also be added to the SDK.

NOTE: You will have to edit the generated recipe based on the comments
contained within it

## Pitfalls

`TARGET_SYS` _must_ be different from `BUILD_SYS`. This is due to the way Rust
configuration options are tracked for different targets. This is the reason
we use the Yocto triples instead of the native Rust triples. See
[Add the ability to provide build flags for the build-script-build #3349](https://github.com/rust-lang/cargo/issues/3349)

## Dependencies

On the host:

* Any `-sys` packages your project might need must have RDEPENDs for
  the native library.

On the target:

* Any `-sys` packages your project might need must have RDEPENDs for
  the native library.
