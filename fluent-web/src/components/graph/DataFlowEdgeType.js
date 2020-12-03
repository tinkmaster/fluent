import React from 'react';
import {getBezierPath, getMarkerEnd} from 'react-flow-renderer';

export const edgeTypes = {
    custom: CustomEdge,
};
export default function CustomEdge({
                                       id,
                                       sourceX,
                                       sourceY,
                                       targetX,
                                       targetY,
                                       sourcePosition,
                                       targetPosition,
                                       style = {},
                                       data,
                                       arrowHeadType,
                                       markerEndId,
                                   }) {
    const edgePath = getBezierPath({sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition});
    const markerEnd = getMarkerEnd(arrowHeadType, markerEndId);
    return (
        <>
            <path id={id} style={style} className="react-flow__edge-path" d={edgePath} markerEnd={markerEnd}/>
            <text>
                <textPath style={{fontSize: '12px'}} startOffset="50%" textAnchor="middle">
                    Aaaaa
                </textPath>
            </text>
        </>
    );
}
