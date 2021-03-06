import {autocomplete} from "bootstrap-autocomplete";
import {SuggestionViewer} from "./SuggestionViewer";


/**
 * Its a high cohesive class to FormQueryEditor.
 * It depends on the dom, which is generated there.
 * So you cant use this class in an other context.
 */
export class MetadataAutocompletion {

    constructor(restApiFetcherServer, graphQlFetcher, fileTypesSelector,
                currentFilterListSelector, currentMetadataListSelector, suggestionWindowOpenerSelector,
    ) {
        this.advancedFilter = null;
        this.attributSelector = null;
        this.restApiFetcherServer = restApiFetcherServer;
        this.graphQlFetcher = graphQlFetcher;
        this.fileTypesSelector = fileTypesSelector;
        this.currentFilterListSelector = currentFilterListSelector;
        this.currentMetadataListSelector = currentMetadataListSelector;

        this.suggestionViewer = new SuggestionViewer(this, restApiFetcherServer, suggestionWindowOpenerSelector);

        this.fileTypes = [];
        this.filter = [];
        this.metadata = [];

    }

    addAdvancedFilter(advancedFilter) {
        this.advancedFilter = advancedFilter;
    }

    addAttributSelector(attributSelector) {
        this.attributSelector = attributSelector;
    }

    getSuggestionViewer() {
        return this.suggestionViewer;
    }

    //public
    addListener() {

        this.suggestionViewer.addListener();
        this.reAddListener();
    }


    // get the Datatype of a given tag from the Server
    getDataType(tag, callback) {

        let thisdata = this;
        let datatype;

        function getFileString() {

            let resultString = tag + "$XXX$";
            thisdata.fileTypes.forEach(element => {
                resultString += element + "$X$";
            });
            return resultString + "$XXX$";
        }


        this.restApiFetcherServer.fetchGet("metadata-autocomplete/datatype/?q=" + encodeURIComponent(getFileString()), function (event) {
            datatype = event.data.toString();
            callback(datatype);
        });
    }


    // get all the file categories and their corresponding file types from the server
    getAllFileCategories(callback) {

        this.restApiFetcherServer.fetchGet("categoryService/", function (event) {
            let fileCategories = event.data;
            callback(fileCategories);
        });
    }

    //private
    updateLists() {

        this.filter = [];
        this.metadata = [];
        this.fileTypes = [];

        let thisdata = this;

        $(this.currentFilterListSelector).each(function () {
            if ($(this).val() === "") return;
            thisdata.filter.push($(this).val());
        });

        $(this.currentMetadataListSelector).each(function () {
            if ($(this).val() === "") return;
            thisdata.metadata.push($(this).val());
        });

        $(this.fileTypesSelector).each(function () {
            if ($(this).val() === "") return;
            thisdata.fileTypes.push($(this).val());
        });

    }

    //private
    getFileString() {

        let resultString = "";
        this.fileTypes.forEach(element => {
            resultString += element + "$X$";
        });

        if (resultString === "") resultString = " ";
        return resultString;
    }


    reAddListener() {

        let thisdata = this;

        function getUsedAsString(type) {

            let existing = thisdata.metadata
            if (type === 0) {
                existing = thisdata.filter;
            }

            let resultString = "";
            existing.forEach(element => {
                resultString += element + "$X$";
            });
            if (resultString === "") resultString = " ";
            return resultString;
        }


        function autoCompleteBuilder(method, func) {
            return {
                preventEnter: true,
                minLength: 0,
                resolverSettings: {
                    url: thisdata.restApiFetcherServer.urlBuilder('metadata-autocomplete/' + method),
                    requestThrottling: 100
                },
                events: {
                    searchPre: func
                }
            }
        }


        $(this.currentFilterListSelector).not(".autocompleteAdded").autoComplete(autoCompleteBuilder("suggestions", value => {
            return value.trim() + "$XXX$" + getUsedAsString(0) + "$XXX$" + this.getFileString();
        }));


        $(this.currentMetadataListSelector).not(".autocompleteAdded").autoComplete(autoCompleteBuilder("suggestions", value => {
            return value.trim() + "$XXX$" + getUsedAsString(1) + "$XXX$" + this.getFileString();
        }));


        $(this.fileTypesSelector).autoComplete(autoCompleteBuilder("filetype-suggestions", value => {
            return value.trim() + "$XXX$" + this.getFileString();
        }));


        function ddShown(event) {
            if ($(this).val() == "") {
                $(this).val(" ");
            }
        }

        function ddHidden(event) {
            if ($(this).val() == " ") {
                $(this).val("");
            }
            $(this).trigger("focusout");
            // thisdata.reAddListener();
            thisdata.updateLists();
        }


        $(this.currentFilterListSelector).not(".autocompleteAdded").on('autocomplete.dd.shown', ddShown);
        $(this.currentFilterListSelector).not(".autocompleteAdded").on('autocomplete.dd.hidden', ddHidden);

        $(this.currentMetadataListSelector).not(".autocompleteAdded").on('autocomplete.dd.shown', ddShown);
        $(this.currentMetadataListSelector).not(".autocompleteAdded").on('autocomplete.dd.hidden', ddHidden);

        $(this.fileTypesSelector).not(".autocompleteAdded").on('autocomplete.dd.shown', ddShown);
        $(this.fileTypesSelector).not(".autocompleteAdded").on('autocomplete.dd.hidden', ddHidden);


        //show the autocomplete directly if the field is selected

        function showDirect() {
            if ($(this).hasClass("autocompleteDeactivated")) return;
            $(this).autoComplete('show');
        }

        $(this.currentFilterListSelector).not(".autocompleteAdded").focusin(showDirect);
        $(this.currentMetadataListSelector).not(".autocompleteAdded").focusin(showDirect);
        $(this.fileTypesSelector).not(".autocompleteAdded").focusin(showDirect);

        $(this.currentFilterListSelector).not(".autocompleteAdded").addClass("autocompleteAdded");
        $(this.currentMetadataListSelector).not(".autocompleteAdded").addClass("autocompleteAdded");
        $(this.fileTypesSelector).not(".autocompleteAdded").not(".autocompleteAdded").addClass("autocompleteAdded");


        // console.log(
        //     '$element events:',
        //     $._data($(this.currentFilterListSelector).get(0), 'events')
        // );

    }

}
