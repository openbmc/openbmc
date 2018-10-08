# meta-google layer

## Purpose

Including this layer adds the following packages to your `obmc-phosphor-image`:

* [google-ipmi-sys](https://github.com/openbmc/google-ipmi-sys): OEM IPMI Handler for providing specific information to the host.
* [phosphor-ipmi-blobs](https://github.com/openbmc/phosphor-ipmi-blobs): OEM IPMI Blobs Handler for providing the framework for specific blob handlers.
* [phosphor-ipmi-ethstats](https://github.com/openbmc/phosphor-ipmi-ethstats): OEM IPMI Handler for reporting ethernet device statistics from the BMCs ethernet devices.

## Customizations

Presently, this layer also enables the Google Iana for registering `phosphor-ipmi-ethstats` in addition to the OpenBMC one.
