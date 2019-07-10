import React from "react";

export default function DocumentView(props) {
  const opennlp = props.selectedDocument.opennlp
    ? props.selectedDocument.opennlp
    : {};

  return (
    <div className="col col-12 px3">
      <h3>Document Content</h3>
      <p>{props.selectedDocument ? props.selectedDocument.doc.content : ""}</p>
      <h3>OpenNLP Results</h3>
      <ul>
        {Object.entries(opennlp).map(([entityType, entityList]) =>
          entityList.map((entity, index) =>
            <li key={index}>
              <b>{entityType}</b>: {entity}
            </li>
          )
        )}
      </ul>
    </div>
  );
}
