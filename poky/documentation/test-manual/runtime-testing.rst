.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

************************************
Performing Automated Runtime Testing
************************************

The OpenEmbedded build system makes available a series of automated
tests for images to verify runtime functionality. You can run these
tests on either QEMU or actual target hardware. Tests are written in
Python making use of the ``unittest`` module, and the majority of them
run commands on the target system over SSH. This section describes how
you set up the environment to use these tests, run available tests, and
write and add your own tests.

For information on the test and QA infrastructure available within the
Yocto Project, see the ":ref:`ref-manual/release-process:testing and quality assurance`"
section in the Yocto Project Reference Manual.

Enabling Tests
==============

Depending on whether you are planning to run tests using QEMU or on the
hardware, you have to take different steps to enable the tests. See the
following subsections for information on how to enable both types of
tests.

Enabling Runtime Tests on QEMU
------------------------------

In order to run tests, you need to do the following:

-  *Set up to avoid interaction with sudo for networking:* To
   accomplish this, you must do one of the following:

   -  Add ``NOPASSWD`` for your user in ``/etc/sudoers`` either for all
      commands or just for ``runqemu-ifup``. You must provide the full
      path as that can change if you are using multiple clones of the
      source repository.

      .. note::

         On some distributions, you also need to comment out "Defaults
         requiretty" in ``/etc/sudoers``.

   -  Manually configure a tap interface for your system.

   -  Run as root the script in ``scripts/runqemu-gen-tapdevs``, which
      should generate a list of tap devices. This is the option
      typically chosen for Autobuilder-type environments.

      .. note::

         -  Be sure to use an absolute path when calling this script
            with sudo.

         -  Ensure that your host has the package ``iptables`` installed.

         -  The package recipe ``qemu-helper-native`` is required to run
            this script. Build the package using the following command::

               $ bitbake qemu-helper-native

-  *Set the DISPLAY variable:* You need to set this variable so that
   you have an X server available (e.g. start ``vncserver`` for a
   headless machine).

-  *Be sure your host's firewall accepts incoming connections from
   192.168.7.0/24:* Some of the tests (in particular DNF tests) start an
   HTTP server on a random high number port, which is used to serve
   files to the target. The DNF module serves
   ``${WORKDIR}/oe-rootfs-repo`` so it can run DNF channel commands.
   That means your host's firewall must accept incoming connections from
   192.168.7.0/24, which is the default IP range used for tap devices by
   ``runqemu``.

-  *Be sure your host has the correct packages installed:* Depending
   your host's distribution, you need to have the following packages
   installed:

   -  Ubuntu and Debian: ``sysstat`` and ``iproute2``

   -  openSUSE: ``sysstat`` and ``iproute2``

   -  Fedora: ``sysstat`` and ``iproute``

   -  CentOS: ``sysstat`` and ``iproute``

Once you start running the tests, the following happens:

#. A copy of the root filesystem is written to ``${WORKDIR}/testimage``.

#. The image is booted under QEMU using the standard ``runqemu`` script.

#. A default timeout of 500 seconds occurs to allow for the boot process
   to reach the login prompt. You can change the timeout period by
   setting
   :term:`TEST_QEMUBOOT_TIMEOUT`
   in the ``local.conf`` file.

#. Once the boot process is reached and the login prompt appears, the
   tests run. The full boot log is written to
   ``${WORKDIR}/testimage/qemu_boot_log``.

#. Each test module loads in the order found in :term:`TEST_SUITES`. You can
   find the full output of the commands run over SSH in
   ``${WORKDIR}/testimgage/ssh_target_log``.

#. If no failures occur, the task running the tests ends successfully.
   You can find the output from the ``unittest`` in the task log at
   ``${WORKDIR}/temp/log.do_testimage``.

Enabling Runtime Tests on Hardware
----------------------------------

The OpenEmbedded build system can run tests on real hardware, and for
certain devices it can also deploy the image to be tested onto the
device beforehand.

For automated deployment, a "controller image" is installed onto the
hardware once as part of setup. Then, each time tests are to be run, the
following occurs:

#. The controller image is booted into and used to write the image to be
   tested to a second partition.

#. The device is then rebooted using an external script that you need to
   provide.

#. The device boots into the image to be tested.

When running tests (independent of whether the image has been deployed
automatically or not), the device is expected to be connected to a
network on a pre-determined IP address. You can either use static IP
addresses written into the image, or set the image to use DHCP and have
your DHCP server on the test network assign a known IP address based on
the MAC address of the device.

In order to run tests on hardware, you need to set :term:`TEST_TARGET` to an
appropriate value. For QEMU, you do not have to change anything, the
default value is "qemu". For running tests on hardware, the following
options are available:

-  *"simpleremote":* Choose "simpleremote" if you are going to run tests
   on a target system that is already running the image to be tested and
   is available on the network. You can use "simpleremote" in
   conjunction with either real hardware or an image running within a
   separately started QEMU or any other virtual machine manager.

-  *"SystemdbootTarget":* Choose "SystemdbootTarget" if your hardware is
   an EFI-based machine with ``systemd-boot`` as bootloader and
   ``core-image-testmaster`` (or something similar) is installed. Also,
   your hardware under test must be in a DHCP-enabled network that gives
   it the same IP address for each reboot.

   If you choose "SystemdbootTarget", there are additional requirements
   and considerations. See the
   ":ref:`test-manual/runtime-testing:selecting systemdboottarget`" section, which
   follows, for more information.

-  *"BeagleBoneTarget":* Choose "BeagleBoneTarget" if you are deploying
   images and running tests on the BeagleBone "Black" or original
   "White" hardware. For information on how to use these tests, see the
   comments at the top of the BeagleBoneTarget
   ``meta-yocto-bsp/lib/oeqa/controllers/beaglebonetarget.py`` file.

-  *"GrubTarget":* Choose "GrubTarget" if you are deploying images and running
   tests on any generic PC that boots using GRUB. For information on how
   to use these tests, see the comments at the top of the GrubTarget
   ``meta-yocto-bsp/lib/oeqa/controllers/grubtarget.py`` file.

-  *"your-target":* Create your own custom target if you want to run
   tests when you are deploying images and running tests on a custom
   machine within your BSP layer. To do this, you need to add a Python
   unit that defines the target class under ``lib/oeqa/controllers/``
   within your layer. You must also provide an empty ``__init__.py``.
   For examples, see files in ``meta-yocto-bsp/lib/oeqa/controllers/``.

Selecting SystemdbootTarget
---------------------------

If you did not set :term:`TEST_TARGET` to "SystemdbootTarget", then you do
not need any information in this section. You can skip down to the
":ref:`test-manual/runtime-testing:running tests`" section.

If you did set :term:`TEST_TARGET` to "SystemdbootTarget", you also need to
perform a one-time setup of your controller image by doing the following:

#. *Set EFI_PROVIDER:* Be sure that :term:`EFI_PROVIDER` is as follows::

      EFI_PROVIDER = "systemd-boot"

#. *Build the controller image:* Build the ``core-image-testmaster`` image.
   The ``core-image-testmaster`` recipe is provided as an example for a
   "controller" image and you can customize the image recipe as you would
   any other recipe.

   Image recipe requirements are:

   -  Inherits ``core-image`` so that kernel modules are installed.

   -  Installs normal linux utilities not BusyBox ones (e.g. ``bash``,
      ``coreutils``, ``tar``, ``gzip``, and ``kmod``).

   -  Uses a custom :term:`Initramfs` image with a custom
      installer. A normal image that you can install usually creates a
      single root filesystem partition. This image uses another installer that
      creates a specific partition layout. Not all Board Support
      Packages (BSPs) can use an installer. For such cases, you need to
      manually create the following partition layout on the target:

      -  First partition mounted under ``/boot``, labeled "boot".

      -  The main root filesystem partition where this image gets installed,
         which is mounted under ``/``.

      -  Another partition labeled "testrootfs" where test images get
         deployed.

#. *Install image:* Install the image that you just built on the target
   system.

The final thing you need to do when setting :term:`TEST_TARGET` to
"SystemdbootTarget" is to set up the test image:

#. *Set up your local.conf file:* Make sure you have the following
   statements in your ``local.conf`` file::

      IMAGE_FSTYPES += "tar.gz"
      IMAGE_CLASSES += "testimage"
      TEST_TARGET = "SystemdbootTarget"
      TEST_TARGET_IP = "192.168.2.3"

#. *Build your test image:* Use BitBake to build the image::

      $ bitbake core-image-sato

Power Control
-------------

For most hardware targets other than "simpleremote", you can control
power:

-  You can use :term:`TEST_POWERCONTROL_CMD` together with
   :term:`TEST_POWERCONTROL_EXTRA_ARGS` as a command that runs on the host
   and does power cycling. The test code passes one argument to that
   command: off, on or cycle (off then on). Here is an example that
   could appear in your ``local.conf`` file::

      TEST_POWERCONTROL_CMD = "powercontrol.exp test 10.11.12.1 nuc1"

   In this example, the expect
   script does the following:

   .. code-block:: shell

      ssh test@10.11.12.1 "pyctl nuc1 arg"

   It then runs a Python script that controls power for a label called
   ``nuc1``.

   .. note::

      You need to customize :term:`TEST_POWERCONTROL_CMD` and
      :term:`TEST_POWERCONTROL_EXTRA_ARGS` for your own setup. The one requirement
      is that it accepts "on", "off", and "cycle" as the last argument.

-  When no command is defined, it connects to the device over SSH and
   uses the classic reboot command to reboot the device. Classic reboot
   is fine as long as the machine actually reboots (i.e. the SSH test
   has not failed). It is useful for scenarios where you have a simple
   setup, typically with a single board, and where some manual
   interaction is okay from time to time.

If you have no hardware to automatically perform power control but still
wish to experiment with automated hardware testing, you can use the
``dialog-power-control`` script that shows a dialog prompting you to perform
the required power action. This script requires either KDialog or Zenity
to be installed. To use this script, set the
:term:`TEST_POWERCONTROL_CMD`
variable as follows::

   TEST_POWERCONTROL_CMD = "${COREBASE}/scripts/contrib/dialog-power-control"

Serial Console Connection
-------------------------

For test target classes requiring a serial console to interact with the
bootloader (e.g. BeagleBoneTarget and GrubTarget),
you need to specify a command to use to connect to the serial console of
the target machine by using the
:term:`TEST_SERIALCONTROL_CMD`
variable and optionally the
:term:`TEST_SERIALCONTROL_EXTRA_ARGS`
variable.

These cases could be a serial terminal program if the machine is
connected to a local serial port, or a ``telnet`` or ``ssh`` command
connecting to a remote console server. Regardless of the case, the
command simply needs to connect to the serial console and forward that
connection to standard input and output as any normal terminal program
does. For example, to use the picocom terminal program on serial device
``/dev/ttyUSB0`` at 115200bps, you would set the variable as follows::

   TEST_SERIALCONTROL_CMD = "picocom /dev/ttyUSB0 -b 115200"

For local
devices where the serial port device disappears when the device reboots,
an additional "serdevtry" wrapper script is provided. To use this
wrapper, simply prefix the terminal command with
``${COREBASE}/scripts/contrib/serdevtry``::

   TEST_SERIALCONTROL_CMD = "${COREBASE}/scripts/contrib/serdevtry picocom -b 115200 /dev/ttyUSB0"

Running Tests
=============

You can start the tests automatically or manually:

-  *Automatically running tests:* To run the tests automatically after the
   OpenEmbedded build system successfully creates an image, first set the
   :term:`TESTIMAGE_AUTO` variable to "1" in your ``local.conf`` file in the
   :term:`Build Directory`::

      TESTIMAGE_AUTO = "1"

   Next, build your image. If the image successfully builds, the
   tests run::

      bitbake core-image-sato

-  *Manually running tests:* To manually run the tests, first globally
   inherit the :ref:`ref-classes-testimage` class by editing your
   ``local.conf`` file::

      IMAGE_CLASSES += "testimage"

   Next, use BitBake to run the tests::

      bitbake -c testimage image

All test files reside in ``meta/lib/oeqa/runtime/cases`` in the
:term:`Source Directory`. A test name maps
directly to a Python module. Each test module may contain a number of
individual tests. Tests are usually grouped together by the area tested
(e.g tests for systemd reside in ``meta/lib/oeqa/runtime/cases/systemd.py``).

You can add tests to any layer provided you place them in the proper
area and you extend :term:`BBPATH` in
the ``local.conf`` file as normal. Be sure that tests reside in
``layer/lib/oeqa/runtime/cases``.

.. note::

   Be sure that module names do not collide with module names used in
   the default set of test modules in ``meta/lib/oeqa/runtime/cases``.

You can change the set of tests run by appending or overriding
:term:`TEST_SUITES` variable in
``local.conf``. Each name in :term:`TEST_SUITES` represents a required test
for the image. Test modules named within :term:`TEST_SUITES` cannot be
skipped even if a test is not suitable for an image (e.g. running the
RPM tests on an image without ``rpm``). Appending "auto" to
:term:`TEST_SUITES` causes the build system to try to run all tests that are
suitable for the image (i.e. each test module may elect to skip itself).

The order you list tests in :term:`TEST_SUITES` is important and influences
test dependencies. Consequently, tests that depend on other tests should
be added after the test on which they depend. For example, since the
``ssh`` test depends on the ``ping`` test, "ssh" needs to come after
"ping" in the list. The test class provides no re-ordering or dependency
handling.

.. note::

   Each module can have multiple classes with multiple test methods.
   And, Python ``unittest`` rules apply.

Here are some things to keep in mind when running tests:

-  The default tests for the image are defined as::

      DEFAULT_TEST_SUITES:pn-image = "ping ssh df connman syslog xorg scp vnc date rpm dnf dmesg"

-  Add your own test to the list of the by using the following::

      TEST_SUITES:append = " mytest"

-  Run a specific list of tests as follows::

     TEST_SUITES = "test1 test2 test3"

   Remember, order is important. Be sure to place a test that is
   dependent on another test later in the order.

Exporting Tests
===============

You can export tests so that they can run independently of the build
system. Exporting tests is required if you want to be able to hand the
test execution off to a scheduler. You can only export tests that are
defined in :term:`TEST_SUITES`.

If your image is already built, make sure the following are set in your
``local.conf`` file::

   INHERIT += "testexport"
   TEST_TARGET_IP = "IP-address-for-the-test-target"
   TEST_SERVER_IP = "IP-address-for-the-test-server"

You can then export the tests with the
following BitBake command form::

   $ bitbake image -c testexport

Exporting the tests places them in the :term:`Build Directory` in
``tmp/testexport/``\ image, which is controlled by the :term:`TEST_EXPORT_DIR`
variable.

You can now run the tests outside of the build environment::

   $ cd tmp/testexport/image
   $ ./runexported.py testdata.json

Here is a complete example that shows IP addresses and uses the
``core-image-sato`` image::

   INHERIT += "testexport"
   TEST_TARGET_IP = "192.168.7.2"
   TEST_SERVER_IP = "192.168.7.1"

Use BitBake to export the tests::

   $ bitbake core-image-sato -c testexport

Run the tests outside of
the build environment using the following::

   $ cd tmp/testexport/core-image-sato
   $ ./runexported.py testdata.json

Writing New Tests
=================

As mentioned previously, all new test files need to be in the proper
place for the build system to find them. New tests for additional
functionality outside of the core should be added to the layer that adds
the functionality, in ``layer/lib/oeqa/runtime/cases`` (as long as
:term:`BBPATH` is extended in the
layer's ``layer.conf`` file as normal). Just remember the following:

-  Filenames need to map directly to test (module) names.

-  Do not use module names that collide with existing core tests.

-  Minimally, an empty ``__init__.py`` file must be present in the runtime
   directory.

To create a new test, start by copying an existing module (e.g.
``oe_syslog.py`` or ``gcc.py`` are good ones to use). Test modules can use
code from ``meta/lib/oeqa/utils``, which are helper classes.

.. note::

   Structure shell commands such that you rely on them and they return a
   single code for success. Be aware that sometimes you will need to
   parse the output. See the ``df.py`` and ``date.py`` modules for examples.

You will notice that all test classes inherit ``oeRuntimeTest``, which
is found in ``meta/lib/oetest.py``. This base class offers some helper
attributes, which are described in the following sections:

Class Methods
-------------

Class methods are as follows:

-  *hasPackage(pkg):* Returns "True" if ``pkg`` is in the installed
   package list of the image, which is based on the manifest file that
   is generated during the :ref:`ref-tasks-rootfs` task.

-  *hasFeature(feature):* Returns "True" if the feature is in
   :term:`IMAGE_FEATURES` or
   :term:`DISTRO_FEATURES`.

Class Attributes
----------------

Class attributes are as follows:

-  *pscmd:* Equals "ps -ef" if ``procps`` is installed in the image.
   Otherwise, ``pscmd`` equals "ps" (busybox).

-  *tc:* The called test context, which gives access to the
   following attributes:

   -  *d:* The BitBake datastore, which allows you to use stuff such
      as ``oeRuntimeTest.tc.d.getVar("VIRTUAL-RUNTIME_init_manager")``.

   -  *testslist and testsrequired:* Used internally. The tests
      do not need these.

   -  *filesdir:* The absolute path to
      ``meta/lib/oeqa/runtime/files``, which contains helper files for
      tests meant for copying on the target such as small files written
      in C for compilation.

   -  *target:* The target controller object used to deploy and
      start an image on a particular target (e.g. Qemu, SimpleRemote,
      and SystemdbootTarget). Tests usually use the following:

      -  *ip:* The target's IP address.

      -  *server_ip:* The host's IP address, which is usually used
         by the DNF test suite.

      -  *run(cmd, timeout=None):* The single, most used method.
         This command is a wrapper for: ``ssh root@host "cmd"``. The
         command returns a tuple: (status, output), which are what their
         names imply - the return code of "cmd" and whatever output it
         produces. The optional timeout argument represents the number
         of seconds the test should wait for "cmd" to return. If the
         argument is "None", the test uses the default instance's
         timeout period, which is 300 seconds. If the argument is "0",
         the test runs until the command returns.

      -  *copy_to(localpath, remotepath):*
         ``scp localpath root@ip:remotepath``.

      -  *copy_from(remotepath, localpath):*
         ``scp root@host:remotepath localpath``.

Instance Attributes
-------------------

There is a single instance attribute, which is ``target``. The ``target``
instance attribute is identical to the class attribute of the same name,
which is described in the previous section. This attribute exists as
both an instance and class attribute so tests can use
``self.target.run(cmd)`` in instance methods instead of
``oeRuntimeTest.tc.target.run(cmd)``.

Installing Packages in the DUT Without the Package Manager
==========================================================

When a test requires a package built by BitBake, it is possible to
install that package. Installing the package does not require a package
manager be installed in the device under test (DUT). It does, however,
require an SSH connection and the target must be using the
``sshcontrol`` class.

.. note::

   This method uses ``scp`` to copy files from the host to the target, which
   causes permissions and special attributes to be lost.

A JSON file is used to define the packages needed by a test. This file
must be in the same path as the file used to define the tests.
Furthermore, the filename must map directly to the test module name with
a ``.json`` extension.

The JSON file must include an object with the test name as keys of an
object or an array. This object (or array of objects) uses the following
data:

-  "pkg" --- a mandatory string that is the name of the package to be
   installed.

-  "rm" --- an optional boolean, which defaults to "false", that specifies
   to remove the package after the test.

-  "extract" --- an optional boolean, which defaults to "false", that
   specifies if the package must be extracted from the package format.
   When set to "true", the package is not automatically installed into
   the DUT.

Here is an example JSON file that handles test "foo" installing
package "bar" and test "foobar" installing packages "foo" and "bar".
Once the test is complete, the packages are removed from the DUT::

     {
         "foo": {
             "pkg": "bar"
         },
         "foobar": [
             {
                 "pkg": "foo",
                 "rm": true
             },
             {
                 "pkg": "bar",
                 "rm": true
             }
         ]
     }

