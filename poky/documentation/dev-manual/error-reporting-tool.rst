.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Using the Error Reporting Tool
******************************

The error reporting tool allows you to submit errors encountered during
builds to a central database. Outside of the build environment, you can
use a web interface to browse errors, view statistics, and query for
errors. The tool works using a client-server system where the client
portion is integrated with the installed Yocto Project
:term:`Source Directory` (e.g. ``poky``).
The server receives the information collected and saves it in a
database.

There is a live instance of the error reporting server at
https://errors.yoctoproject.org.
When you want to get help with build failures, you can submit all of the
information on the failure easily and then point to the URL in your bug
report or send an email to the mailing list.

.. note::

   If you send error reports to this server, the reports become publicly
   visible.

Enabling and Using the Tool
===========================

By default, the error reporting tool is disabled. You can enable it by
inheriting the :ref:`ref-classes-report-error` class by adding the
following statement to the end of your ``local.conf`` file in your
:term:`Build Directory`::

   INHERIT += "report-error"

By default, the error reporting feature stores information in
``${``\ :term:`LOG_DIR`\ ``}/error-report``.
However, you can specify a directory to use by adding the following to
your ``local.conf`` file::

   ERR_REPORT_DIR = "path"

Enabling error
reporting causes the build process to collect the errors and store them
in a file as previously described. When the build system encounters an
error, it includes a command as part of the console output. You can run
the command to send the error file to the server. For example, the
following command sends the errors to an upstream server::

   $ send-error-report /home/brandusa/project/poky/build/tmp/log/error-report/error_report_201403141617.txt

In the previous example, the errors are sent to a public database
available at https://errors.yoctoproject.org, which is used by the
entire community. If you specify a particular server, you can send the
errors to a different database. Use the following command for more
information on available options::

   $ send-error-report --help

When sending the error file, you are prompted to review the data being
sent as well as to provide a name and optional email address. Once you
satisfy these prompts, the command returns a link from the server that
corresponds to your entry in the database. For example, here is a
typical link: https://errors.yoctoproject.org/Errors/Details/9522/

Following the link takes you to a web interface where you can browse,
query the errors, and view statistics.

Disabling the Tool
==================

To disable the error reporting feature, simply remove or comment out the
following statement from the end of your ``local.conf`` file in your
:term:`Build Directory`::

   INHERIT += "report-error"

Setting Up Your Own Error Reporting Server
==========================================

If you want to set up your own error reporting server, you can obtain
the code from the Git repository at :yocto_git:`/error-report-web/`.
Instructions on how to set it up are in the README document.

