# cron-parser
Simple maven project for parsing cron string and determinig if the cron job will take place during given interval.

Running - run the main class CronChecker.  Expected arguments:

<pre> &lt;cron string&gt; &lt;from timestamp&gt; &lt;to timestamp&gt;</pre>

For examples of inputs and results see <tt>CronCheckerTest</tt>.

Allowed cron string values are from http://unixhelp.ed.ac.uk/CGI/man-cgi?crontab+5

Requires java 8.

<h2>Implementation description</h2>

Cron string looks like <minutes> <hours> <day of month> <month> <day of week> (for more detailed description see http://unixhelp.ed.ac.uk/CGI/man-cgi?crontab+5)

<h3>Algorithm</h3>

Cron string is split into fields. For each field are the allowed values found - we are looking for values which satisfy the condition from input cron string and are withing the bounds allowed for the field. So each field contains list of allowed values (represents as integers). 

Having this allowed values we can start deciding if the an event will take place during given interval or not. We go from year parts of date to minutes and compare the part of given dates with allowed values for this field - we decide if allowed values are within bound or not. If yes, we can move to next part of date.

<h3>Important classes</h3>

<h4>package cz.cron.fields</h4>
Each class represents one field from cron string.

<code>Field.java</code> contains logic for processing cron string. 

<h4>package cz.cron</h4>

In this packages are classes for creating cron string objects and for deciding if an event will take place during given interval or not. 

<code>CronChecker.java</code> - class with main method 

<code>Cron.java</code> - holds cron string fields, decides if the event will take place during given interval or not. 
