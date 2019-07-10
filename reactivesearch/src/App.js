import React from "react";
import {
  ReactiveBase,
  DataSearch,
  ReactiveList,
  SingleList
} from "@appbaseio/reactivesearch";
import SearchResultList from "./components/SearchResultList";
import DocumentView from "./components/DocumentView";
import { getUrlParams } from "./utils";

export default class App extends React.Component {
  state = {
    selectedDocument: "",
    elastic_index: getUrlParams("appname"),
    elastic_url: getUrlParams("url")
  };

  onSelectDocument = res => {
    this.setState({ selectedDocument: res });
  };

  render() {
    return (
      <div>
        <ReactiveBase
          app={this.state.elastic_index}
          url={this.state.elastic_url}
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
                    autosuggest
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
