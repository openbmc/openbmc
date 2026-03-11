# IP Compliance

## linux-firmware-rpidistro

By default, some of the machine configurations recommend packages for the
WiFi/BT firmware, provided by
[linux-firmware-rpidistro](https://github.com/RPi-Distro/firmware-nonfree).
This package includes some firmware blobs under the `Synaptics` license which
could carry a legal risk: one of the clauses can be (at least theoretically)
used as a `killswitch`. This was
[reported](https://github.com/RPi-Distro/firmware-nonfree/issues/29) in the
upstream repository.

You can find the full license text body in the content of the above mentioned
package.

Due to the above, the build system will only allow this recipe to be built if
the user acknowledges this risk by adding the following configuration:

    LICENSE_FLAGS_ACCEPTED = "synaptics-killswitch"

You can provide this configuration as part of your `local.conf`, `distro.conf`,
etc.
