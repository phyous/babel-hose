babel-hose
=====
<center><img src="/images/tower-of-babel-dark-big.jpg" height="300px"/></center>

> "Now the whole world had one language and a common speech."
> 	-- Genesis 11:1

Twitters [streaming API](https://dev.twitter.com/docs/streaming-apis) hooked up to [Bing translator](http://www.bing.com/translator/) allowing you to understand what's happening everywhere in the world in real time in your native language.
This project leverages the recently released [hosebird](https://github.com/twitter/hbc) framework for easy access to the [sample](https://dev.twitter.com/docs/api/1.1/get/statuses/sample) and [filter](https://dev.twitter.com/docs/api/1.1/post/statuses/filter) streams.

## Setup
Before you can get started, you'll need developer accounts for both Twitter and Bing Translator.

1- Create a Twitter account first if you don't have one. [Click here](https://twitter.com/)

2- Create a Twitter application. [Click here](https://dev.twitter.com/apps/new)

3- Create a Azure application. Follow instructions [here](http://www.restlessprogrammer.com/2013/03/setting-up-free-bing-translator-api.html).

4- Add Bing & Twitter credentials to babel-hose/src/main/resources/org/phyous/babelhose/settings.json

5- You'll need java and maven installed. See [here](http://www.mkyong.com/maven/how-to-install-maven-in-windows/) for windows and [here](http://maven.apache.org/download.cgi) for everyone else.

6- Compile the code:
```mvn clean install```

## Usage Examples
* Get help:
> java -jar target/babel-hose-1.0-jar-with-dependencies.jar -help
```text
usage: babel-hose
 -f,--filterList <arg>   Comma delimited list of strings to filter tweets
                         by. Defaults to null.
 -h,--help               Display help.
 -l,--language <arg>     Language to translate to. Defaults to 'en'. See
                         here for supported list:
                         http://msdn.microsoft.com/en-us/library/hh456380.
                         aspx
 -m,--mode <arg>         Processing mode for tweets. Defaults to 'hose'.
 -n,--numTweets <arg>    The number of tweets to process before exiting.
                         Defaults to 1000.
 -q,--qps <arg>          Queries per second. Defaults to 2.0.
```

* Translate the 100 most recent non-english tweets into english (show ~3 tweets a second):
> java -jar target/babel-hose-1.0-jar-with-dependencies.jar -qps 3 -language=en -numTweets=100
```text
--- @erikbertaina : Mon Mar 04 06:19:53 +0000 2013 ---> [Location: argentina]
[es] Esperando, simplemente, viendo cómo el tiempo pasa sin que pase nada http://t.co/To9ms7DSGx =>
[en] Waiting for you, just seeing how time goes by without you pass nothing http://t.co/To9ms7DSGx
```

* Help the French understand what yolo means:
> java -jar target/babel-hose-1.0-jar-with-dependencies.jar -filterList="#yolo" -language=fr
```text
--- @itsjaylin : Mon Mar 04 06:24:46 +0000 2013 ---> [Location: Austin, Texas]
[en] Forget #YOLO, its all about them #ROLO's. =>
[fr] Oubliez #YOLO, son tout environ de leur #ROLO.
```

* Help the Americans understand european football:
> java -jar target/babel-hose-1.0-jar-with-dependencies.jar -filterList="fútbol" -language=en
```text
--- @jonaspekee : Mon Mar 04 06:25:58 +0000 2013 ---> [Location: Guayaquil-Ecuador]
[es] RT @RMFutbolOnline: Didier Drogba: "La mayoría de los fans del Barcelona, son niños quienes vieron fútbol ayer por primera vez en su vida". =>
[en] RT @RMFutbolOnline: Didier Drogba: "the majority of the Barcelona fans, are children who saw football yesterday for the first time in his life".
```

* A special debug mode that shows discrepancies in translations of twitter entities (@/#/$/Url)
> java -jar target/babel-hose-1.0-jar-with-dependencies.jar -m entity
