# cron-parser
Simple maven project for parsing cron string and determinig if the cron job will take place during given interval.

Running - run the main class CronChecker.  Expected arguments:

<pre> &lt;cron string&gt; &lt;from timestamp&gt; &lt;to timestamp&gt;</pre>

Cron string allowed values are from http://unixhelp.ed.ac.uk/CGI/man-cgi?crontab+5

requires java 8
