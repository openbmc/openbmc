.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Setting up a Hash Equivalence Server
************************************

A :ref:`overview-manual/concepts:Hash Equivalence` server can help reduce build
times by using the mechanism described in the :ref:`overview-manual/concepts:Hash Equivalence`
section of the Yocto Project Overview and Concepts Manual.

This document will guide you through the steps required to set up the reference
Hash Equivalence server provided by the :oe_git:`bitbake-hashserv
</bitbake/tree/bin/bitbake-hashserv>` script in :term:`BitBake`.

This guide will explain how to setup a local Hash Equivalence server and does
not explain how to setup the surrounding infrastructure to secure this server.

Hash Equivalence Server Setup
=============================

From this point onwards, the commands displayed below are assumed to be run from
the :term:`BitBake` repository, which can be found :oe_git:`here </bitbake>`.

To start a basic Hash Equivalence server, one could simply run::

   ./bin/bitbake-hashserv

This will take all of the default options of the script, which are already
sufficient to start a local server.

Run ``./bin/bitbake-hashserv --help`` to see what options the script can take.
Some of the important ones are:

-  ``--database`` controls the location of the hash server database (default:
   ``./hashserver.db``).

-  ``--bind`` controls the bind address of the server (default:
   ``unix://./hashserver.sock``).

   You can specify three types of addresses:

   -  ``unix://PATH``: will bind to a Unix socket at ``PATH``.

   -  ``wss://ADDRESS:PORT``: will bind to a Websocket at ``ADDRESS:PORT``.

   -  ``ADDRESS:PORT``: will bind to a raw TCP socket at ``ADDRESS:PORT``.

-  ``--log`` can be used to control the logging level of the server (e.g.
   ``INFO`` will print information about clients connection to the server).

-  ``--upstream`` can be used to specify an upstream server to pull hashes from.
   This has no default value, meaning no upstream server is used.

-  ``--db-username`` and ``--db-password`` can be used to control the access to
   the database.

-  ``--read-only`` will disable hash reports from clients.

These variables can also be set from the environment from where it is being run.
Run ``./bin/bitbake-hashserv --help`` to get the variable names that you can
export.

.. warning::

   The shared Hash Equivalence server needs to be maintained together with the
   :ref:`Shared State <overview-manual/concepts:Shared State>` cache. Otherwise,
   the server could report Shared State hashes that only exist on specific
   clients.

   We therefore recommend that one Hash Equivalence server be set up to
   correspond with a given Shared State cache, and to start this server
   in *read-only mode* (``--read-only`` option), so that it doesn't store
   equivalences for Shared State caches that are local to clients.

   If there is no pre-existing Shared State Cache, the server should allow
   hashes to be reported (no ``--read-only`` option) to create the initial
   Hash Equivalence database.

Yocto Project Build Setup
=========================

To use the server started in the previous section, set the following variables
in a :term:`configuration file`::

   BB_HASHSERVE = "<bind address>"
   BB_SIGNATURE_HANDLER = "OEEquivHash"

The ``<bind address>`` value should be replaced to point to the server started
in the previous section.

See the documentation of :term:`BB_SIGNATURE_HANDLER` for more details on this
variable.

You can optionally specify an upstream server with :term:`BB_HASHSERVE_UPSTREAM`
variable. For example::

   BB_HASHSERVE_UPSTREAM = "wss://hashserv.yoctoproject.org/ws"

This will make the local server pull hashes from the upstream server. The
:term:`BB_HASHSERVE_UPSTREAM` only works when a server is running
(:term:`BB_HASHSERVE` is set).

To output debugging information on what is happening with Hash Equivalence when
builds are started, you can configure :term:`BitBake` logging as follows from a
:term:`configuration file`::

   BB_LOGCONFIG = "hashequiv.json"

With ``hashequiv.json`` containing the following content:

.. code-block:: json

   {
      "version": 1,
      "loggers": {
         "BitBake.SigGen.HashEquiv": {
            "level": "VERBOSE",
            "handlers": ["BitBake.verbconsole"]
         },
         "BitBake.RunQueue.HashEquiv": {
            "level": "VERBOSE",
            "handlers": ["BitBake.verbconsole"]
         }
      }
   }

This will make Hash Equivalence related changes be printed on the console, such
as::

   NOTE: Task <recipe.bb>:do_<task> unihash changed to dc0da29c62a2d78d8d853fbb9c173778fe7d6fa4a68c67494b17afffe8ca1894
