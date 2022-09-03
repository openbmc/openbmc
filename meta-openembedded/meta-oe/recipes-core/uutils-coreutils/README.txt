How to generate/update the file uutils-coreutils_XXX.bb:

cargo with version > 1.60 is needed so cargo from Ubuntu's apt will not work
(because of https://github.com/rust-lang/cargo/issues/10623):
This package is needed (tested on Ubuntu 22.04):
sudo apt-get -y install librust-cargo+openssl-dev

Then install cargo-bitbake with:
$ cargo install --locked cargo-bitbake

You can now update coreutils:
$ git clone https://github.com/uutils/coreutils.git
$ cd coreutils
$ git tag
$ git checkout 0.0.XXX
$ cargo-bitbake bitbake
Wrote: coreutils_0.0.15.bb

Verify manual changes in the bb file (rename coreutils.inc to uutils-coreutils.inc)
