import os
import pathlib
import subprocess
import sys
import webbrowser


class Test:
    def __init__(self, test_file_path):
        self.test_file_path = test_file_path
        self.name = test_file_path.stem
        self.passed = False

    def get_source_file_path(self):
        return self.test_file_path.with_suffix('.source')

    def get_expect_file_path(self):
        return self.test_file_path.with_suffix('.expect')

    def get_result_file_path(self):
        return self.test_file_path.with_suffix('.result')


def create_html_row_for_test(test):
    if test.passed:
        return f'''
            <tr class="pass-row">
                <th>{test.name}</th>
                <th>Pass</th>
                <th>N/A</th>
            </tr>
        '''
    else:
        return f'''
            <tr class="fail-row">
                <th>{test.name}</th>
                <th>Fail</th>
                <th>
                    <ul>
                        <li><a href="{test.get_source_file_path()}">{test.get_source_file_path()}</a></li>
                        <li><a href="{test.get_expect_file_path()}">{test.get_expect_file_path()}</a></li>
                        <li><a href="{test.get_result_file_path()}">{test.get_result_file_path()}</a></li>
                    </ul>
                </th>
            </tr>
        '''

def create_and_view_html_results(tests):
    rows = ''
    for test in tests:
        rows += create_html_row_for_test(test)
    html = f'''
        <html>
            <head>
                <style>
                    * {{
                        box-sizing: border-box;
                        margin: 0px;
                        padding: 0px;
                        font-family: sans-serif;
                    }}

                    body {{
                        background-color: lightgrey;
                    }}

                    #container {{
                        margin: 20px auto;
                        padding: 10px;
                        width: 80%;
                        background-color: burlywood;
                        border: 1px solid black;
                    }}

                    #title {{
                        margin-bottom: 20px;
                    }}

                    #total {{
                        margin-bottom: 10px;
                    }}

                    #results-table {{
                        width: 100%;
                    }}

                    #results-table thead th {{
                        background-color: lightblue;
                    }}

                    #results-table th {{
                        padding: 5px;
                        text-align: left;
                        vertical-align: middle;
                    }}

                    #results-table ul {{
                        list-style-position: inside;
                    }}

                    .pass-row {{
                        background-color: olivedrab;
                    }}

                    .fail-row {{
                        background-color: orangered;
                    }}
                </style>
            </head>
            <body>
                <div id="container">
                    <h1 id="title">Test Results</h1>
                    <h3 id="total">{sum(1 for test in tests if not test.passed)} of {len(tests)} tests failed.</h3>
                    <table id="results-table" border="1">
                        <thead>
                            <tr>
                                <th>Test Name</th>
                                <th>Result</th>
                                <th>Details</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows}
                        </tbody>
                    </table>
                </div>
            </body>
        </html>
    '''
    html_file_path = get_cwd() / 'results.html'
    write_to_file(html_file_path, html)
    webbrowser.open(html_file_path)

def extract_source_and_expect(test_file_path):
        source = ''
        expect = ''
        with open(test_file_path, 'r') as test_file:
            hasReadExpect = False
            for line in test_file:
                if line == '---* EXPECT *---\n':
                    hasReadExpect = True
                    continue
                if not hasReadExpect:
                    source += line
                else:
                    expect += line
        return (source, expect)

def run_jocks_and_get_output(source_file_path):
        jvm = 'C:\\Users\\angus\\.jdks\\openjdk-24.0.1\\bin\\java.exe' # Requires changing if JDK moves
        return subprocess.run(
            [jvm, '-cp', get_cwd() / '../target/classes/', 'com.colossalg.Jocks', source_file_path],
            capture_output=True,
            text=True
        ).stdout

def get_cwd():
    return pathlib.Path(__file__).resolve().parent

def write_to_file(file_path, content):
    with open(file_path, 'w') as file:
        file.write(content)

def remove_files_if_exist(file_paths):
    for file_path in file_paths:
        if os.path.exists(file_path):
            os.remove(file_path)

def run_tests():
    tests = [Test(test_file_path) for test_file_path in get_cwd().glob('*.test')]
    for test in tests:
        source, expect = extract_source_and_expect(test.test_file_path)
        write_to_file(test.get_source_file_path(), source)
        result = run_jocks_and_get_output(test.get_source_file_path())
        if result == expect:
            test.passed = True
            remove_files_if_exist([
                test.get_source_file_path(),
                test.get_expect_file_path(),
                test.get_result_file_path()
            ])
        else:
            test.passed = False
            write_to_file(test.get_expect_file_path(), expect)
            write_to_file(test.get_result_file_path(), result)
    create_and_view_html_results(tests)

def clean():
    to_remove = (
        list(get_cwd().glob('*.html')) +
        list(get_cwd().glob('*.source')) +
        list(get_cwd().glob('*.expect')) +
        list(get_cwd().glob('*.result'))
    )
    remove_files_if_exist(to_remove)

if __name__ == '__main__':
    match sys.argv[1:]:
        case []:
            run_tests()
        case ['--clean']:
            clean()
        case _:
            print('USAGE:')
            print('    python test_runner.py [--clean]')