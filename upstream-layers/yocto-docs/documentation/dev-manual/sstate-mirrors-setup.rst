.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Setting up a Shared State Cache Mirror
**************************************

This document explains how to set up a server that hosts shared state artifacts
that can be reused by machines connecting to the server.

The concept of "shared state" is explained in the
:ref:`overview-manual/concepts:Setscene Tasks and Shared State` section of the
Yocto Project Overview and Concepts Manual. These artifacts are files that are
by default placed in the shared state directory (:term:`SSTATE_DIR`).

This document explains how to share the content of this directory through the
use of the :term:`SSTATE_MIRRORS` variable.

Use-case
========

The most common use-case for setting up a shared state cache mirror is usually
when there is a dedicated machine in an infrastructure building images at a
regular interval, thus populating a shared state cache directory frequently.
Usually, this machine is part of a :wikipedia:`CI/CD <CI/CD>` type of
environment.

Since the shared state directory of this machine contains most of the items
needed to accelerate a new build, it can be interesting to share it to remote
clients connecting to the machine and fetching the artifacts over the network:

.. code-block:: text

                      +---------------------+
                      |                     |
           +----------+    Build Machine    +-------------+
           |          |                     |             |
           |          +--------+------------+             |
           |                   |                          |
           |                   |                          |
           |                   |                          |
           |                   |                          |
           |                   |                          |
   +-------v------+    +-------v------+           +-------v------+
   |              |    |              |           |              |
   |   Client 1   |    |   Client 2   |   . . .   |   Client N   |
   |              |    |              |           |              |
   +--------------+    +--------------+           +--------------+

With this kind of setup, it is assumed that:

-  Clients connect to the server over some protocol such as HTTP. In practice,
   the protocol should be one supported by BitBake (see :doc:`the supported
   fetchers <bitbake:bitbake-user-manual/bitbake-user-manual-fetching>`).

-  The shared state directory (:term:`SSTATE_DIR`) of the build machine is
   is read-only from the point of view of the clients.

-  The previous points also means that clients hold their own copy of the shared
   state artifacts in their own shared state directory.

Server Setup
============

There are many ways of setting up a file hosting server. To illustrate, this
document will use a basic HTTP server started thanks to the ``http.server``
Python module.

On our build machine, we assume that the server shares the shared state from the
:term:`Build Directory`:

.. code-block:: text

   build/
   ├── ...
   ├── sstate-cache/
   └── ...

With the ``http.server`` Python module, the server can be started as follows:

.. code-block:: console

   $ python3 -m http.server -d .../build/sstate-cache
   Serving HTTP on 0.0.0.0 port 8000 (http://0.0.0.0:8000/) ...

Client Configuration
====================

Configuring clients to this server happens through a :term:`configuration file`,
for example, the :ref:`site.conf <structure-build-conf-site.conf>` file. Only
the :term:`SSTATE_MIRRORS` variable is needed to setup the connection::

   SSTATE_MIRRORS = "file://.* http://127.0.0.1:8000/PATH;downloadfilename=PATH"

The line above depends on your server configuration. Here, the example uses the
local ``127.0.0.1`` IP address (the client is running on the same machine) and
the files are served on the port 8000.

With this, the client is configured to download shared state artifacts from the
server. This will happen automatically based on the :ref:`signatures
<overview-manual/concepts:Checksums (Signatures)>` of the tasks which are about
to run (so only relevant artifacts are fetched).

After running a build, the local shared state directory of the client
(configured through its :term:`SSTATE_DIR` variable), will be populated with the
files downloaded from the server.

Going Further
=============

-  It is recommended to setup a :ref:`overview-manual/concepts:Hash Equivalence`
   service on the build machine --- running in parallel of the shared state
   mirror --- to further speed up builds. See the
   :doc:`/dev-manual/hashequivserver` section of the Yocto Project Development
   Tasks Manual for more information.

-  If the mirror is protected behind a username and password, the
   :term:`build host` needs to be configured so the :term:`build system
   <OpenEmbedded Build System>` is able to download the shared state cache using
   authentication.

   The recommended way to do that is by setting the following parameters
   in ``$HOME/.netrc`` (``$HOME`` being the :term:`build host` home
   directory)::

      machine someserver.tld
      login <user>
      password <password>

   This file requires permissions set to ``400`` or ``600`` to prevent
   other users from reading the file:

   .. code-block:: console

      $ chmod 600 "$HOME/.netrc"

   Another method to configure the username and password is from the
   URL in :term:`SSTATE_MIRRORS` directly, with the ``user`` and ``pswd``
   parameters::

      SSTATE_MIRRORS ?= "\
          file://.* https://someserver.tld/share/sstate/PATH;user=<user>;pswd=<password>;downloadfilename=PATH \
      "

-  If the :term:`BB_NO_NETWORK` variable is set to "1" on clients as a means to
   disable any accesses to the network, the :term:`SSTATE_MIRROR_ALLOW_NETWORK`
   variable may be used to allow the clients to fetch from the shared state
   mirror as an exception.

-  Multiple shared state sources can be specified in the :term:`SSTATE_MIRRORS`
   variable. For example::

      SSTATE_MIRRORS = "\
         file://.* https://someserver.com/PATH;downloadfilename=PATH \
         file://.* https://someotherserver.com/PATH;downloadfilename=PATH \
      "

   The shared state artifacts will be searched on the remote locations in the
   same order as they are specified in this variable.

-  Fetching the shared state artifacts from a local directory, such as an
   :wikipedia:`NFS <Network_File_System>`-mounted directory, is also possible
   using the ``file://`` fetcher::

      SSTATE_MIRRORS = "file://.* file:///path/to/shared-state/PATH;downloadfilename=PATH"

-  The shared state mirror may be used in combination with GPG signatures to
   ensure the authenticity of the downloaded files. See the
   :doc:`/security-manual/sstate-signing` section of the Yocto Project Security
   Manual for more information.

-  The size of the shared state directory on the server may grow over time. The
   :oecore_path:`sstate-cache-management.py <scripts/sstate-cache-management.py>`
   script may be used to remove duplicate files. Otherwise, removing files
   that have not been accessed for a certain period of time can be done with the
   standard ``find`` command, e.g. for removing files older than 2 months:

   .. code-block:: console

      $ find sstate-cache/ -type f -atime +60 -delete

Troubleshooting
===============

The best way to check that files are being downloaded from the server is
probably to check the server logs. For example, using the Python ``http.server``
module, the following logs are shown:

.. code-block:: text

   127.0.0.1 - - [20/Jul/2026 14:36:23] "HEAD /d1/75/sstate%3Afile%3Ax86-64-v3-oe-linux%3A5.48%3Ar0%3Ax86-64-v3%3A14%3Ad175b18b8adc9cc3003edfc62f112fd421505bc715d41f36b4d04eef05d226ad_packagedata.tar.zst HTTP/1.1" 200 -
   127.0.0.1 - - [20/Jul/2026 14:36:23] "HEAD /f4/11/sstate%3Aos-release%3Aall-oe-linux%3A1.0%3Ar0%3Aallarch%3A14%3Af411b2f27319283694ab13bf0d8965a7dd26428431418dfb7505f3d1cdd0dbfd_package_write_ipk.tar.zst HTTP/1.1" 404 -

Some artifacts may be found (returning 200 above). Some others will not be found
because the expected artifacts on the client is not present on the server, in
which case the client will rebuild the task (and its dependencies).
