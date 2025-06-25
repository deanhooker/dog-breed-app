# Dog Breed App

A simple app using the dog.ceo API to display a list of dog breeds and images of these breeds. Built with ClojureScript, [reagent](https://github.com/reagent-project/reagent) and [re-frame](https://github.com/day8/re-frame).

## Running the App

Clone the repository and:

### Option 1: `npm` not installed

Open public/index.html in a browser.

### Option 2: use `npm`

Run `npx shadow-cljs watch app` in the project's root directory. This will start an HTTP server at http://localhost:8000 while watching the ClojureScript source files, re-compiling to JavaScript when changes occur.

## Design Notes

This application uses Reagent to render React components and re-frame for managing application state. State is contained in a single atom and is updated via re-frame's event handlers. Components subscribe to parts of the state and are updated automatically as the state changes.
