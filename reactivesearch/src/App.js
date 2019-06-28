import React from "react";
import {
  ReactiveBase,
  DataSearch,
  ReactiveList,
  SingleList
} from "@appbaseio/reactivesearch";
import SearchResultList from "./components/SearchResultList";
import DocumentView from "./components/DocumentView";

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedDocument: ""
    };
  }

  onSelectDocument = res => {
    this.setState({ selectedDocument: res.doc.content });
  };

  render() {
    return (
      <div>
        <ReactiveBase
          app={process.env.REACT_APP_ELASTIC_INDEX}
          url={process.env.REACT_APP_ELASTIC_URL}
        >
          <div className="container">
            <div className="clearfix">
              <div className="col col-12 px3">
                <h2>Reactive Search Example</h2>
              </div>
              <div className="col col-6 px3">
                <div className="col col-12">
                  {/* which documents contain this term */}
                  <p>Data Search: </p>
                  <DataSearch
                    componentId="mainSearch"
                    // we will likely be using the opennlp fields instead here
                    // or extracted portions of a document
                    dataField={["doc.content"]}
                    className="search-bar"
                    queryFormat="and"
                    placeholder="Search..."
                    iconPosition="left"
                    autosuggest={true}
                    filterLabel="search"
                  />
                </div>
                {/* Sorting and Aggregations
                what is the value of this field for this document? */}
                <SingleList
                  componentId="Locations"
                  dataField="opennlp.locations"
                  title="Locations"
                />
              </div>
              <div className="col col-6 px3">
                <ReactiveList
                  componentId="results"
                  dataField="res.azurestorage.blob"
                  react={{
                    and: ["mainSearch"]
                  }}
                  pagination
                  paginationAt="bottom"
                  pages={5}
                  size={4}
                  Loader="Loading..."
                  noResults="No results were found..."
                  renderItem={res => (
                    <SearchResultList
                      key={res._id}
                      response={res}
                      onSelectDocument={this.onSelectDocument}
                    />
                  )}
                />
              </div>
              <DocumentView selectedDocument={this.state.selectedDocument} />
            </div>
          </div>
        </ReactiveBase>
      </div>
    );
  }
}

export default App;
