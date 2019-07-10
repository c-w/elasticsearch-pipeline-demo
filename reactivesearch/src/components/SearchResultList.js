import React from "react";

export default class SearchResultList extends React.Component {
  onClick = () => this.props.onSelectDocument(this.props.response);

  render() {
    return (
      <div>
        <p className="border-top mb3" onClick={this.onClick}>
          <b>file</b>: {this.props.response.azurestorage.blob}
        </p>
      </div>
    );
  }
}
