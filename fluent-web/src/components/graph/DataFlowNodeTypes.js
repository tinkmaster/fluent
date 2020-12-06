import React, {memo} from 'react';
import {Handle} from 'react-flow-renderer';
import {Col, Popover, Row} from "antd";
import ClockCircleOutlined from "@ant-design/icons/lib/icons/ClockCircleOutlined";

export const runningNode = memo(({data}) => {
    return (
        <div style={{
            border: '2px solid green',
            borderRadius: 6,
            padding: "18px 30px",
            animation: 'glow 1800ms ease-out infinite alternate',
            minWidth: '180px'
        }}>
            <Handle
                type="target"
                position="left"
            />
            <div>
                {getNodeLabel(data.label)}
            </div>
            <Handle
                type="source"
                position="right"
            />
        </div>
    );
});

export const errorNode = memo(({data}) => {
    return (
        <div style={{
            border: '3px solid black',
            borderRadius: 6,
            padding: "18px 30px",
            backgroundColor: '#db8b8b',
            minWidth: '180px'
        }}>
            <Handle
                type="target"
                position="left"
            />
            <div>
                {getNodeLabel(data.label)}
            </div>
            <Handle
                type="source"
                position="right"
            />
        </div>
    );
});

export const finishedNode = memo(({data}) => {
    return (
        <div style={{
            border: '2px solid black',
            borderRadius: 6,
            padding: "6px 30px",
            minWidth: '180px'
        }}>
            <Handle
                type="target"
                position="left"
            />
            <div>
                <Row style={{testAlign: 'center'}}>
                    {getNodeLabel(data.label)}
                </Row>
                <Row>
                    <Col offset={4}>
                        <ClockCircleOutlined/>
                    </Col>
                    <Col offset={3}>
                        {data.usedTime} ms
                    </Col>
                </Row>
            </div>
            <Handle
                type="source"
                position="right"
            />
        </div>
    );
});

export const todoNode = memo(({data}) => {
    return (
        <div style={{
            border: '2px dashed #8ff2f5',
            borderRadius: 6,
            padding: "15px 25px",
            minWidth: '180px'
        }}>
            <Handle
                type="target"
                position="left"
            />
            <div>
                {getNodeLabel(data.label)}
            </div>
            <Handle
                type="source"
                position="right"
            />
        </div>
    );
});

export const pipelineNode = memo(({data}) => {
    return (
        <div style={{
            border: '2px solid black',
            borderRadius: 6,
            padding: "15px 25px",
            minWidth: '180px'
        }}>
            <Handle
                type="target"
                position="left"
            />
            <div>
                {getNodeLabel(data.label)}
            </div>
            <Handle
                type="source"
                position="right"
            />
        </div>
    );
});

function getNodeLabel(label) {
    if (label.length > 20) {
        return (
            <div>
                <Popover content={label}>
                    {label.substr(0, 20)}
                </Popover>
            </div>
        )
    } else {
        return (
            <div>
                {label}
            </div>
        )
    }
}

export const nodeTypes = {
    runningNode: runningNode,
    errorNode: errorNode,
    todoNode: todoNode,
    finishedNode: finishedNode,
    pipelineNode: pipelineNode
};



