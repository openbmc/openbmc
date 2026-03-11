.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Limiting the Host Resources Usage
*********************************

While you sometimes need to :doc:`speed up a build
</dev-manual/speeding-up-build>`, you may also need to limit the resources used
by the :term:`OpenEmbedded Build System`, especially on shared infrastructures
where multiple users start heavy-load builds, or when building on low-power
machines.

This document aims at giving the different configuration variables available to
limit the resources used by the build system. These variables should be set from
a :term:`configuration file` and thus take effect over the entire build environment.
For each variable, also see the variable description in the glossary for more
details.

-  :term:`BB_NUMBER_THREADS`:

   This sets a hard limit on the number of threads :term:`BitBake` can run at the
   same time. Lowering this value will set a limit to the number of
   :term:`BitBake` threads, but will not prevent a single task from starting more
   compilation threads (see :term:`PARALLEL_MAKE`).

-  :term:`BB_NUMBER_PARSE_THREADS`:

   Like :term:`BB_NUMBER_THREADS`, but this variable sets a limit on the number
   of threads during the parsing of the environment (before executing tasks).

-  :term:`PARALLEL_MAKE`:

   This variable should be set in the form of ``-jN``, where ``N`` is a positive
   integer. This integer controls the number of threads used when starting
   ``make``. Note that this variable is not limited to the usage of ``make``,
   but extends to the compilation (:ref:`ref-tasks-compile` task) commands
   defined by the :ref:`ref-classes-meson`, :ref:`ref-classes-cmake` and such
   classes.

   If you want to have a different limit from the rest of the build for a
   recipe, it is also possible to achieve with the following line added to your
   ``local.conf`` :term:`configuration file`::

      PARALLEL_MAKE:pn-linux-yocto = "-j4"

   The above example will limit the number of threads used by ``make`` for the
   ``linux-yocto`` recipe to 4.

-  :term:`PARALLEL_MAKEINST`:

   Like :term:`PARALLEL_MAKE`, but this variable controls the number of threads
   used during the :ref:`ref-tasks-install` task.

   The default value of :term:`PARALLEL_MAKEINST` is the value of
   :term:`PARALLEL_MAKE`.

.. note::

   While most of the variables in this document help to limit the CPU load, it
   is also possible that the host system runs out of physical RAM when running
   builds. This can trigger the out-of-memory killer and stop the related
   processes abruptly. This can create strange looking failures in the output
   log of the tasks in question. The out-of-memory killer only logs in the
   kernel dmesg logs, so it is advised to monitor it closely with the ``dmesg``
   command when encountering unexpected failures during builds.

   In these situations, lowering the value of :term:`PARALLEL_MAKE` and
   :term:`BB_NUMBER_THREADS` is recommended.

-  :term:`BB_PRESSURE_MAX_CPU`, :term:`BB_PRESSURE_MAX_IO` and
   :term:`BB_PRESSURE_MAX_MEMORY`:

   These variables control the limit of pressure (PSI as defined by
   https://docs.kernel.org/accounting/psi.html) on the system, and will
   limit the number of :term:`BitBake` threads dynamically depending on the
   current pressure of the system. This also means that your host must support
   the PSI kernel feature (otherwise see :term:`BB_LOADFACTOR_MAX` below).

   These variables take a positive integer between 1 (extremely low limit) and
   1000000 (value unlikely ever reached). Setting an extremely low value, such
   as 2, is not desirable as it will result in :term:`BitBake` limiting the
   number of threads to 1 most of the time.

   To determine a reasonable value to set for your host, follow the steps below:

   #. In a Bash shell, start the following script, which will provide an
      estimate of the current pressure on your host:

      .. code-block:: bash

         pressure="0"
         while true; do
            prev_pressure="$pressure"
            pressure=$(head -1 /proc/pressure/cpu  | cut -d' ' -f5 | cut -d'=' -f2)
            echo $(( $pressure - $prev_pressure ))
            sleep 1
         done

      .. note::

         Change ``/proc/pressure/cpu`` to ``/proc/pressure/io`` or
         ``/proc/pressure/memory`` to change the pressure type to monitor.

      This script can be stopped by pressing Control + C.

   #.  Then, start a heavy-load build, for example::

          bitbake virtual/kernel -c compile -f

       You can stop the build at anytime with Control + C.

   #.  Monitor the values printed on the console. These should indicate how the
       pressure evolves during the build. You can take a value below the maximum
       printed value as a starting point.

   After setting initial values, :term:`BitBake` will print messages on the
   console in the following format each time the current pressure exceeds of the
   limit set by the above variables::

      Pressure status changed to CPU: True, IO: False, Mem: False (CPU: 1105.9/2.0, IO: 0.0/2.0, Mem: 0.0/2.0) - using 1/64 bitbake threads

   Take a look at the value between parenthesis: ``CPU: 1105.9/2.0, IO: 0.0/2.0,
   Mem: 0.0/2.0``. They correspond to the current pressure value for the CPU, IO
   and memory respectively. If :term:`BitBake` prints these messages a lot, it
   is likely that your pressure limit is too low, and thus can be raised to a
   higher value.

-  :term:`BB_LOADFACTOR_MAX`:

   This variable will limit the number of threads :term:`BitBake` will start
   by monitoring the current CPU load of the host system. :term:`BitBake` will
   print the following when the limit set by :term:`BB_LOADFACTOR_MAX` is
   reached::

      Load average limiting set to True as load average: 0.7188262939453125 - using 37/64 bitbake threads

   This variable has no effect when any of :term:`BB_PRESSURE_MAX_CPU`,
   :term:`BB_PRESSURE_MAX_IO` or :term:`BB_PRESSURE_MAX_MEMORY` is set, as it
   was designed for systems that do not have pressure information available.
