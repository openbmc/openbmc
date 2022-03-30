meta-parsec layer
==============

This layer contains recipes for the Parsec service and parsec tools.

Dependencies
============

This layer depends on:

    URI: git://git.openembedded.org/meta-openembedded
    branch: master

    URI git://git.yoctoproject.org/meta-security
    branch: master

    URI https://github.com/kraj/meta-clang.git
    branch: master

Adding the meta-parsec layer to your build
==========================================

In order to use this layer, you need to make the build system aware of it.

You can add it to the build system by adding the
location of the meta-parsec layer to bblayers.conf, along with any
other layers needed. e.g.:

    BBLAYERS ?= " \
      /path/to/yocto/meta \
      /path/to/yocto/meta-yocto \
      /path/to/yocto/meta-yocto-bsp \
      /path/to/meta-openembedded/meta-oe \
      /path/to/meta-openembedded/meta-python \
      /path/to/meta-clang \
      /path/to/meta-security/meta-tpm \
      /path/to/meta-security/meta-parsec \
      "

To include the Parsec service into your image add following into the
local.conf:

    IMAGE_INSTALL:append = " parsec-service"

  By default the Parsec service will be deployed into the image with
TPM, PKCS11, MBED-CRYPTO and CRYPTOAUTHLIB providers build in
and with the default config file from the Parsec repository:
https://github.com/parallaxsecond/parsec/blob/main/config.toml

  You can use PACKAGECONFIG for Parsec servic recipe to define
what providers should be built in. For example,

    PACKAGECONFIG:pn-parsec-service = "TPM"

  The default Parsec service config file contains the MbedCrypto provider
enabled. The config file needs to be updated to use the Parsec service
with other providers like TPM or PKCS11. The required procedures are
covered in Parsec documentation.
https://parallaxsecond.github.io/parsec-book/

Updating recipes
================

  The parsec-service and parsec-tool recipes use include files with lists
of all rust crates required. This allows bitbake to fetch all the necessary
dependent crates, as well as a pegged version of the crates.io index,
to ensure maximum reproducibility.
  It's recommended to use cargo-bitbake to generate include files for new
versions of parsec recipes.
https://github.com/meta-rust/cargo-bitbake

  When you have crago-bitbake built:
1. Checkout the required version of parsec repository.
2. Run cargo-bitbake inside the repository. It will produce a BB file.
3. Create a new include file with SRC_URI and LIC_FILES_CHKSUM from the BB file.

Manual testing with runqemu
===========================

  This layer also contains a recipe for pasec-tool which can be used for
manual testing of the Parsec service:

    IMAGE_INSTALL:append = " parsec-tool"

  There are a series of Parsec Demo videos showing how to use parsec-tool
to test the Parsec service base functionality:
https://www.youtube.com/watch?v=ido0CyUdMHM&list=PLKjl7IFAwc4S7WQqqphCsyy6DPDxJ2Skg&index=4

  The parsec-tool recipe also includes `parsec-cli-tests.sh` script
which runs e2e tests against all providers enabled and configured
in Parsec service.

  You can use runqemu to start a VM with a built image file and run
manual tests with parsec-tool.

Enabling Parsec providers for manual testing
============================================

1. MbedCrypto provider
  The default Parsec service config file contains the MbedCrypto provider
enabled. No changes required.

2. PKCS11 provider
  The Software HSM can be used for manual testing of the provider by
including it into your test image:

    IMAGE_INSTALL:append = " softhsm"

Inside the running VM:
- Stop Parsec
```bash
systemctl stop parsec
```
- Initialise a token and notice the result slot number
```bash
softhsm2-util --init-token --slot 0 --label "Parsec Service" --pin 123456 --so-pin 123456
```
- Change the token ownership:
```bash
for d in /var/lib/softhsm/tokens/*; do chown -R parsec $d; done
```
- Enable the PKCS11 provider and update its parameters in the Parsec config file
/etc/parsec/config.toml
```
library_path = "/usr/lib/softhsm/libsofthsm2.so"
slot_number = <slot number>
user_pin = "123456"
```
- Start Parsec
```bash
systemctl start parsec
```

3. TPM provider
  The IBM Software TPM service can be used for manual testing of the provider by
including it into your test image:

    IMAGE_INSTALL:append = " ibmswtpm2 tpm2-tools libtss2 libtss2-tcti-mssim"

Inside the running VM:
- Stop Parsec
```bash
systemctl stop parsec
```
- Start and configure the Software TPM server
```bash
   /usr/bin/tpm_server &
   sleep 5
   /usr/bin/tpm2_startup -c -T mssim
   /usr/bin/tpm2_changeauth -c owner tpm_pass
```
- Enable the TPM provider and update its parameters in the Parsec config file
/etc/parsec/config.toml
```
tcti = "mssim"
owner_hierarchy_auth = "hex:74706d5f70617373"
```
- Start Parsec
```bash
systemctl start parsec
```

Maintenance
-----------

Send pull requests, patches, comments or questions to yocto@yoctoproject.org

When sending single patches, please using something like:
'git send-email -1 --to yocto@lists.yoctoproject.org --subject-prefix=meta-parsec][PATCH'

These values can be set as defaults for this repository:

$ git config sendemail.to yocto@lists.yoctoproject.org
$ git config format.subjectPrefix meta-parsec][PATCH

Now you can just do 'git send-email origin/master' to send all local patches.

Maintainers:    Anton Antonov <Anton.Antonov@arm.com>
                Armin Kuster <akuster808@gmail.com>


License
=======

All metadata is MIT licensed unless otherwise stated. Source code included
in tree for individual recipes is under the LICENSE stated in each recipe
(.bb file) unless otherwise stated.
