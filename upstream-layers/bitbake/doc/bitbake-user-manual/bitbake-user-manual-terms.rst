.. SPDX-License-Identifier: CC-BY-2.5

*************
BitBake Terms
*************

Below is a list of terms and definitions common to BitBake.

.. glossary::

   :term:`Datastore`
      The datastore is a dictionary-like object represented by the ``DataSmart``
      class in BitBake (in ``lib/bb/data_smart.py``) and contains the values of
      variables under the current execution of BitBake. As explained in
      :ref:`bitbake-user-manual/bitbake-user-manual-execution:Locating and
      Parsing Recipes`, BitBake parses recipes, bbappends, and classes and
      stores the values of various variables into the :term:`datastore`.

      The datastore is available by default in recipes and classes under the
      name ``d``.

      The :term:`datastore` defines several helper functions to read, write or
      manipulate BitBake variables, which are documented in the
      :ref:`bitbake-user-manual/bitbake-user-manual-metadata:Functions for
      Accessing Datastore Variables` section.
