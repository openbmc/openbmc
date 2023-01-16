.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release 4.2 (mickledore)
========================

Migration notes for 4.2 (mickledore)
------------------------------------

This section provides migration information for moving to the Yocto
Project 4.2 Release (codename "mickledore") from the prior release.

.. _migration-4.2-python-3.8:

Python 3.8 is now the minimum required Python version version
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~------------

BitBake and OpenEmbedded-Core are now relying on Python 3.8,
making it a requirement to use a distribution providing at least this
version, or to use :term:`buildtools`.

.. _migration-4.2-qa-checks:

QA check changes
~~~~~~~~~~~~~~~~

.. _migration-4.2-misc-changes:

Miscellaneous changes
~~~~~~~~~~~~~~~~~~~~~

- The ``OEBasic`` signature handler (see :term:`BB_SIGNATURE_HANDLER`) has been
  removed.

.. _migration-4.2-removed-variables:

Removed variables
~~~~~~~~~~~~~~~~~

The following variables have been removed:
- ``SERIAL_CONSOLE``, deprecated since version 2.6, replaced by :term:``SERIAL_CONSOLES``.

.. _migration-4.2-removed-recipes:

Removed recipes
~~~~~~~~~~~~~~~

The following recipes have been removed in this release:

