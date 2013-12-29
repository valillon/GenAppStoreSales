GenAppStoreSales
================

Java code for managing sales reports generated in App Store by the iTunesConnect server.

1. Downloads scheduled App Store sales reports.
2. Generates charts from reports.


Reports are available during a limited period of time according to their class (daily, weekly, monthly and yearly). Beyond the expiration time they are deleted and can't be no longer downloaded from the Apple's servers.

Note <code>appID</code>, <code>email</code>, <code>password</code> and <code>appSKU</code> variables need to be updated according to your itunesconnect account. Further explanation of this code <a href="http://thisshouldbethetitle.blogspot.com.es/2013/04/java-snippet-for-generating-scheduled.html">here</a>.


- Additional required files (both):
<ol>
<li><em>Autoingestion.class</em> file linked in this  <a href="http://www.apple.com/itunesnews/docs/AppStoreReportingInstructions.pdf">
PDF</a>.</li>
<li><a href="http://www.jfree.org/jfreechart/download.html">
FreeChart Java Library</a> (testing version 1.0.14).</li>
</ol>

- Instructions to schedule a program launcher in OSX <a href="http://thisshouldbethetitle.blogspot.com.es/2013/04/notifying-daily-appsetore-reports-with.html">here</a>.


- Report availability, content, format... in
<a href="http://www.apple.com/itunesnews/docs/AppStoreReportingInstructions.pdf"> App Store reporting Instructions</a>.



MIT License 2013 - Rafael Redondo Tejedor
