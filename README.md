# stromer

A Clojure playground for a stream-mining Hackday at the [BBUZZ-2014](http://berlinbuzzwords.de/hackathons-meetups) you can still
register [here](http://lanyrd.com/2014/data-stream-mining-hackathon/).

We want to work on some stream-mining algorithms in different languages ( [clojure](https://github.com/sojoner/stromer)
[scala](https://github.com/knutwalker/bbuzz14-stream-mining) and [python](https://github.com/truemped/bbhack-2014)),
mining a stream of tweets.

All of these repository's should help you get started to get some tweets and work with them, since we try to save
as much setup-time as we can.

## Usage

### checkout

    git clone https://github.com/sojoner/stromer

### compile

    lein compile

### run the examples

    lein run

### open the repl

    lein repl

## Codepoints

in src/stromer/core.clj you will find 3 examples

* example1 will need the twitter oauth credentials and than open a stream
* example2 will scan redis keys for tweet.json objects infinitely or how many times you want to loop through them
* example3 will scroll through an provided elasticsearch index of tweets

You can of course edit the tweet sources in the following files.

* src/stromer/sources/redis.clj
* src/stromer/sources/elastic_search.clj
* src/stromer/sources/twitter.clj

All you need to do is choose a source that will work for you, remove the dummy print callback and implement your
own callback function which will than do great stream-mining.

## License

Copyright © 2014 @Sojoner
Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
