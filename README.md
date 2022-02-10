# imjur

Upload and share your images. Made with Clojure and ClojureScript.

## Installation

Clone from (https://github.com/EdwardIII/imjur/)

Run with:

    lein run

Or build the jar for release with:

    lein uberjar

The output jar will be in ``

## Usage

Start up a webserver listening on http://localhost:3000

    java -jar imjur-0.1.0-standalone.jar

Drag images into the "Drag it here". They will be uploaded and
publically accessible via `/uploads/<filename>`. For example,
if you uploaded `cat.jpg`, that will be available via `/uploads/cat.jpg`.

## Tests

    # Clojure tests
    lein test

    # ClojureScript tests
    lein fig:test


## License

See [LICENSE](LICENSE)
