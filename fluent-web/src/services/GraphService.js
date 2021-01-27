export function drawExecutionGraph(data) {
    let res = []
    let nodesHaveDrawn = {}
    let branchesNum = {}  // record nodes number in each level to split them in the graph
    if (data.sources.length > 0) {
        for (let i = 0; i < data.sources.length; i++) {
            drawNextNodes(data, null, data.sources[i], 1, branchesNum, res, nodesHaveDrawn);
            branchesNum['num'] = branchesNum['num'] + 1
        }
    }
    return res;
}

function drawNextNodes(data, parentId, id, pathLength, branchesNum, res, nodesHaveDrawn) {
    const node = data.nodes[id]
    if (!branchesNum['num']) {
        branchesNum['num'] = 1
    }

    if (!nodesHaveDrawn[id]) {
        let obj = {}
        Object.assign(obj, {
            id: '' + id,
            data: {
                label: id + '-' + node.operator.name,
                name: node.operator.name,
                usedTime: node.usedTime
            },
            position: {x: 250 * pathLength, y: 80 + 80 * (branchesNum['num'] - 0.5)}
        })
        if (node.status === 'RUNNING') {
            obj['type'] = 'runningNode'
        } else if (node.status === 'FAILED') {
            obj['type'] = 'errorNode'
        } else if (node.status === 'FINISHED') {
            obj['type'] = 'finishedNode'
        } else {
            obj['type'] = 'todoNode'
        }
        res.push(obj)
        nodesHaveDrawn[id] = 'true'

        // draw the connections
        if (parentId) {
            res.push(
                {
                    id: parentId + '->' + id,
                    source: parentId + '',
                    target: id + '',
                    sourceHandler: undefined,
                    targetHandler: undefined,
                    arrowHeadType: 'arrowclosed',
                    type: 'custom',
                    animated: true
                }
            )
        }
    } else {
        let connectionExisted = false
        for (let i = 0; i < res.length; i++) {
            if (res[i].id === id) {
                res[i].position = {x: 250 * pathLength, y: 80 + 80 * (branchesNum['num'] - 1)}
            }
            if (res[i].id === parentId + '->' + id) {
                connectionExisted = true
            }
        }
        if (!connectionExisted && parentId) {
            res.push(
                {
                    id: parentId + '->' + id,
                    source: parentId,
                    target: id,
                    sourceHandler: null,
                    targetHandler: null,
                    arrowHeadType: 'arrowclosed',
                    type: 'custom',
                    animated: true
                }
            )
        }

    }


    if (node.next.length > 0) {
        for (let i = 0; i < node.next.length; i++) {
            branchesNum['num'] = branchesNum['num'] + i
            drawNextNodes(data, id, node.next[i], pathLength + 1, branchesNum, res, nodesHaveDrawn);
        }
    }
}


export function drawPipelineGraph(data) {
    let sources = []
    let nodes = {}

    const ids = Object.keys(data.operators)
    ids.map(id => {
            nodes[id] = {
                next: [],
                upstream: [],
                operator: {
                    name: data.operators[id]
                }
            }
        }
    )

    data.connections.map(con => {
        const ns = con.split('->')
        nodes[parseInt(ns[0])].next.push(parseInt(ns[1]))
        nodes[parseInt(ns[1])].upstream.push(parseInt(ns[0]))
    })

    ids.map(id => {
            if (nodes[id].upstream.length === 0) {
                sources.push(id)
            }
        }
    )

    return drawExecutionGraph({sources: sources, nodes: nodes});
}
