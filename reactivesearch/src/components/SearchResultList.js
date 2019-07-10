import React from "react";

export default function SearchResultList(props) {
  const response = props.response;
  return (
    <div>
      <p
        className="border-top mb3"
        onClick={() => props.onSelectDocument(response)}
      >
        <b>file</b>: {response.azurestorage.blob}
      </p>
    </div>
  );
}
