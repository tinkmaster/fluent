import React, {Component} from 'react';
import {Button, List, Popconfirm} from "antd";
import {DeleteOutlined, EditOutlined, PlusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import './css/OperatorList.css'

export class OperatorList extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return (
            <div className="operator-list">
                <List
                    header={<div className="operator-list-title">
                        <span>Operator list</span>
                        <Button
                            onClick={() => this.props.updatePipelinePageState('selectedOperator', null, 'operatorDetailsVisible', true
                            )}
                            style={{'marginLeft': 'auto', 'marginRight': '18px'}}><PlusOutlined/></Button>
                    </div>}
                    dataSource={this.props.operators}
                    renderItem={item => (
                        <List.Item className="operator-list-item">
                            <span style={{'width': 'fit-content'}}>{item}</span>
                            <PlusCircleOutlined className="operator-list-item-icon"
                                                style={{'marginLeft': 'auto'}}
                                                onClick={() => this.props.addOperatorToPipelineGraph(item)}/>
                            <EditOutlined className="operator-list-item-icon" style={{'paddingLeft': 18,}}
                                          onClick={(e) => {
                                              this.props.editOperator(e, item)
                                          }}/>
                            <Popconfirm
                                placement="bottomRight"
                                title={'Are you sure to delete this operator?'}
                                onConfirm={() => this.props.deleteOperator(item)}
                                okText="Yes"
                                cancelText="No"
                                style={{backgroundColor: 'white', color: 'white'}}
                            >
                                <DeleteOutlined className="operator-list-item-icon" style={{'paddingLeft': 18,}}/>
                            </Popconfirm>

                        </List.Item>
                    )}
                />
            </div>
        )
    }
}
